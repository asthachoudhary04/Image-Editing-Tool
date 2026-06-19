import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.util.Stack;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class AdvancedImageEditor extends JFrame {

    private BufferedImage original;
    private BufferedImage edited;

    private Stack<BufferedImage> undoStack = new Stack<>();

    private JLabel label;
    private Point cropStart;
    private Point cropEnd;

    private boolean cropMode = false;
    private double zoom = 1.0;

    private JSlider brightness = new JSlider(-100, 100, 0);
    private JSlider contrast = new JSlider(-100, 100, 0);
    private JSlider saturation = new JSlider(-100, 100, 0);
    private JSlider structure = new JSlider(-100, 100, 0);
    private JSlider ambience = new JSlider(-100, 100, 0);
    private JSlider whitePoint = new JSlider(-100, 100, 0);
    private JSlider highlights = new JSlider(-100, 100, 0);
    private JSlider shadows = new JSlider(-100, 100, 0);
    private JSlider warmth = new JSlider(-100, 100, 0);

    public AdvancedImageEditor() {

        ThemeManager.applyTheme();
        setTitle("CapCut Style Image Editor");
        setSize(1200, 800);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (edited != null) {
                    int choice = JOptionPane.showOptionDialog(
                            AdvancedImageEditor.this,
                            "Do you want to save your work before leaving?",
                            "Save Session",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Save & Exit", "Exit Without Saving", "Cancel"},
                            "Save & Exit"
                    );
                    if (choice == 0) {
                        saveImage();
                        System.exit(0);
                    } else if (choice == 1) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        setLayout(new BorderLayout());

        // IMAGE VIEW
        label = new JLabel(){
            @Override
            protected void paintComponent(Graphics g){

                super.paintComponent(g);

                if(cropMode &&
                        cropStart != null &&
                        cropEnd != null){

                    int x = Math.min(cropStart.x,cropEnd.x);
                    int y = Math.min(cropStart.y,cropEnd.y);

                    int w = Math.abs(cropStart.x-cropEnd.x);
                    int h = Math.abs(cropStart.y-cropEnd.y);

                    g.setColor(Color.RED);

                    ((Graphics2D)g).setStroke(
                            new BasicStroke(2));

                    g.drawRect(x,y,w,h);
                }
            }
        };

        add(new JScrollPane(label), BorderLayout.CENTER);

        label.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e){

                if(cropMode){

                    cropStart = e.getPoint();
                    cropEnd = e.getPoint();

                    label.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e){

                if(cropMode){

                    cropEnd = e.getPoint();

                    label.repaint();

                    performCrop();
                }
            }
        });

        label.addMouseMotionListener(
                new MouseMotionAdapter(){

                    @Override
                    public void mouseDragged(
                            MouseEvent e){

                        if(cropMode){

                            cropEnd = e.getPoint();

                            label.repaint();
                        }
                    }
                });

        // TOP PANEL
        JPanel top = new JPanel();

        JButton open = new JButton("Open");
        JButton save = new JButton("Save");
        JButton zoomIn = new JButton("+ Zoom");
        JButton zoomOut = new JButton("- Zoom");
        JButton reset = new JButton("Reset");
        JButton undo = new JButton("↩ Undo");

        top.add(open);
        top.add(save);
        top.add(zoomIn);
        top.add(zoomOut);
        top.add(reset);
        top.add(undo);

        ThemeManager.stylePanel(top, false);
        ThemeManager.styleButton(open, true);
        ThemeManager.styleButton(save, true);
        ThemeManager.styleButton(zoomIn, false);
        ThemeManager.styleButton(zoomOut, false);
        ThemeManager.styleButton(reset, false);
        ThemeManager.styleButton(undo, false);

        add(top, BorderLayout.NORTH);

        // RIGHT PANEL (TABS STYLE)
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JButton filterTab = new JButton("🎨 Filters");
        JButton aiTab = new JButton("🤖 AI");
        JButton effectTab = new JButton("✨ Effects");
        JButton cropTab = new JButton("✂ Crop & Transform");

        right.add(filterTab);
        right.add(aiTab);
        right.add(effectTab);
        right.add(cropTab);

        ThemeManager.stylePanel(right, false);
        ThemeManager.styleButton(filterTab, false);
        ThemeManager.styleButton(aiTab, false);
        ThemeManager.styleButton(effectTab, false);
        ThemeManager.styleButton(cropTab, false);

        JPanel filterPanel = new JPanel(new GridLayout(5,1));
        JPanel aiPanel = new JPanel(new GridLayout(5,1));
        JPanel effectPanel = new JPanel(new GridLayout(5,1));
        JPanel cropPanel = new JPanel(new GridLayout(10,1));


        filterPanel.setVisible(false);
        aiPanel.setVisible(false);
        effectPanel.setVisible(false);
        cropPanel.setVisible(false);

        // FILTERS
        JButton gray = new JButton("Grayscale");
        JButton sepia = new JButton("Sepia");
        JButton invert = new JButton("Invert");
        JButton blur = new JButton("Blur");
        JButton sharp = new JButton("Sharpen");
        JButton denoise = new JButton("Denoise");
        JButton unblur = new JButton("Unblur");
        JButton pop = new JButton("Detail Pop");

        filterPanel.add(gray);
        filterPanel.add(sepia);
        filterPanel.add(invert);
        filterPanel.add(blur);
        filterPanel.add(sharp);
        filterPanel.add(denoise);
        filterPanel.add(unblur);
        filterPanel.add(pop);

        ThemeManager.stylePanel(filterPanel, true);
        ThemeManager.styleButton(gray, false);
        ThemeManager.styleButton(sepia, false);
        ThemeManager.styleButton(invert, false);
        ThemeManager.styleButton(blur, false);
        ThemeManager.styleButton(sharp, false);
        ThemeManager.styleButton(denoise, false);
        ThemeManager.styleButton(unblur, false);
        ThemeManager.styleButton(pop, false);

        // AI
        JButton ai = new JButton("AI Enhance");
        JButton portraitBlurBtn = new JButton("Portrait Blur");
        aiPanel.add(ai);
        aiPanel.add(portraitBlurBtn);
        ThemeManager.stylePanel(aiPanel, true);
        ThemeManager.styleButton(ai, true);
        ThemeManager.styleButton(portraitBlurBtn, true);

        // EFFECTS
        JButton glow = new JButton("Glow");
        JButton vintage = new JButton("Vintage");
        JButton hdr = new JButton("HDR");
        JButton soft = new JButton("Soft Light");

        effectPanel.add(glow);
        effectPanel.add(vintage);
        effectPanel.add(hdr);
        effectPanel.add(soft);

        ThemeManager.stylePanel(effectPanel, true);
        ThemeManager.styleButton(glow, false);
        ThemeManager.styleButton(vintage, false);
        ThemeManager.styleButton(hdr, false);
        ThemeManager.styleButton(soft, false);


        right.add(filterPanel);
        right.add(aiPanel);
        right.add(effectPanel);
        right.add(cropPanel);

        //CROP

        JButton cropBtn = new JButton("Crop");

        JButton rotate90 = new JButton("Rotate 90°");
        JButton rotate180 = new JButton("Rotate 180°");
        JButton rotate270 = new JButton("Rotate 270°");
        JButton rotate360 = new JButton("Rotate 360°");

        JButton mirrorH = new JButton("Mirror Horizontal");
        JButton mirrorV = new JButton("Mirror Vertical");

        cropPanel.add(cropBtn);

        cropPanel.add(rotate90);
        cropPanel.add(rotate180);
        cropPanel.add(rotate270);
        cropPanel.add(rotate360);

        cropPanel.add(mirrorH);
        cropPanel.add(mirrorV);

        ThemeManager.stylePanel(cropPanel, true);
        ThemeManager.styleButton(cropBtn, true);
        ThemeManager.styleButton(rotate90, false);
        ThemeManager.styleButton(rotate180, false);
        ThemeManager.styleButton(rotate270, false);
        ThemeManager.styleButton(rotate360, false);
        ThemeManager.styleButton(mirrorH, false);
        ThemeManager.styleButton(mirrorV, false);

        add(right, BorderLayout.EAST);

        // BOTTOM PANEL
         structure = new JSlider(-100, 100, 0);
         ambience = new JSlider(-100, 100, 0);
         whitePoint = new JSlider(-100, 100, 0);
         highlights = new JSlider(-100, 100, 0);
         shadows = new JSlider(-100, 100, 0);
         warmth = new JSlider(-100, 100, 0);

        JPanel bottom = new JPanel(new BorderLayout());
        ThemeManager.stylePanel(bottom, false);

// SLIDER TOGGLE BUTTON ROW
        JPanel sliderToggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        sliderToggleRow.setBackground(ThemeManager.BG_PANEL);

        String[] labels = {"Brightness","Contrast","Saturation","Structure",
                "Ambience","White Point","Highlights","Shadows","Warmth"};
        JSlider[] sliders = {brightness,contrast,saturation,structure,
                ambience,whitePoint,highlights,shadows,warmth};

// EXPANDED SLIDER PANEL (hidden by default)
        JPanel sliderGrid = new JPanel(new GridLayout(9, 2, 4, 4));
        sliderGrid.setBackground(ThemeManager.BG_PANEL);
        sliderGrid.setVisible(false);

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            ThemeManager.styleLabel(lbl, true);
            sliderGrid.add(lbl);
            ThemeManager.styleSlider(sliders[i]);
            sliderGrid.add(sliders[i]);
        }

// ONE TOGGLE BUTTON PER SLIDER
        for (int i = 0; i < labels.length; i++) {
            JButton toggleBtn = new JButton(labels[i]);
            ThemeManager.styleButton(toggleBtn, false);
            toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            final int index = i;
            toggleBtn.addActionListener(e -> {
                boolean visible = sliderGrid.isVisible();
                // scroll to and highlight the right slider
                sliderGrid.setVisible(true);
                // highlight the clicked slider label
                for (Component c : sliderGrid.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel jl = (JLabel) c;
                        jl.setForeground(
                                jl.getText().equals(labels[index])
                                        ? ThemeManager.ACCENT
                                        : ThemeManager.TEXT_MUTED
                        );
                    }
                }
                bottom.revalidate();
                bottom.repaint();
            });

            sliderToggleRow.add(toggleBtn);
        }

// HIDE BUTTON
        JButton hideSliders = new JButton("▲ Hide");
        ThemeManager.styleButton(hideSliders, false);
        hideSliders.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hideSliders.addActionListener(e -> {
            sliderGrid.setVisible(!sliderGrid.isVisible());
            hideSliders.setText(sliderGrid.isVisible() ? "▲ Hide" : "▼ Adjust");
        });
        sliderToggleRow.add(hideSliders);

        bottom.add(sliderToggleRow, BorderLayout.NORTH);
        bottom.add(sliderGrid, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);


        // EVENTS
        open.addActionListener(e -> openImage());
        save.addActionListener(e -> saveImage());

        zoomIn.addActionListener(e -> { zoom += 0.1; apply(); });
        zoomOut.addActionListener(e -> { zoom = Math.max(0.1, zoom - 0.1); apply(); });

        reset.addActionListener(e -> resetImage());
        undo.addActionListener(e -> undoLastChange());

        // TAB TOGGLE
        filterTab.addActionListener(
                e -> filterPanel.setVisible(!filterPanel.isVisible())
        );
        aiTab.addActionListener(
                e -> aiPanel.setVisible(!aiPanel.isVisible())
        );
        effectTab.addActionListener(
                e -> effectPanel.setVisible(!effectPanel.isVisible())
        );
        cropTab.addActionListener(
                e -> cropPanel.setVisible(!cropPanel.isVisible())
        );

        // FILTER ACTIONS
        gray.addActionListener(e -> { undoStack.push(copy(edited)); edited = toGray(copy(edited)); apply(); });
        sepia.addActionListener(e -> { undoStack.push(copy(edited)); edited = toSepia(copy(edited)); apply(); });
        invert.addActionListener(e -> { undoStack.push(copy(edited)); edited = toInvert(copy(edited)); apply(); });
        blur.addActionListener(e -> { undoStack.push(copy(edited)); edited = toBlur(copy(edited)); apply(); });
        sharp.addActionListener(e -> { undoStack.push(copy(edited)); edited = toSharpen(copy(edited)); apply(); });

        //Actions
        denoise.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = denoise(copy(edited));
            apply();
        });

        unblur.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = unblur(copy(edited));
            apply();
        });

        pop.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = detailPop(copy(edited));
            apply();
        });

        // AI + EFFECTS
        ai.addActionListener(e -> { undoStack.push(copy(edited)); edited = aiEnhance(copy(edited)); apply(); });
        glow.addActionListener(e -> { undoStack.push(copy(edited)); edited = glow(copy(edited)); apply(); });
        vintage.addActionListener(e -> { undoStack.push(copy(edited)); edited = vintage(copy(edited)); apply(); });
        hdr.addActionListener(e -> { undoStack.push(copy(edited)); edited = hdr(copy(edited)); apply(); });
        soft.addActionListener(e -> { undoStack.push(copy(edited)); edited = softLight(copy(edited)); apply(); });

        //CROP

        cropBtn.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Drag mouse over image to crop");

            cropMode = true;
        });

        //MIRROR IMAGE

        mirrorH.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = mirrorHorizontal(edited);
            apply();
        });

        mirrorV.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = mirrorVertical(edited);
            apply();
        });

        //ROTATE IMAGE

        rotate90.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = rotate(edited, 90);
            apply();
        });

        rotate180.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = rotate(edited, 180);
            apply();
        });

        rotate270.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = rotate(edited, 270);
            apply();
        });

        rotate360.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = rotate(edited, 360);
            apply();
        });

        portraitBlurBtn.addActionListener(e -> {
            undoStack.push(copy(edited));
            edited = portraitBlur(copy(edited));
            apply();
        });


        brightness.addChangeListener(e -> apply());
        contrast.addChangeListener(e -> apply());
        saturation.addChangeListener(e -> apply());
        structure.addChangeListener(e -> apply());
        ambience.addChangeListener(e -> apply());
        whitePoint.addChangeListener(e -> apply());
        highlights.addChangeListener(e -> apply());
        shadows.addChangeListener(e -> apply());
        warmth.addChangeListener(e -> apply());

        setVisible(true);
    }

    // ---------------- OPEN ----------------
    private void openImage() {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                original = ImageIO.read(ch.getSelectedFile());
                edited = copy(original);
                zoom = 1.0;

                brightness.setValue(0);
                contrast.setValue(0);
                saturation.setValue(0);

                apply();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // ---------------- SAVE ----------------
    private void saveImage() {
        try {
            if (edited == null) return;

            JFileChooser ch = new JFileChooser();
            if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = ch.getSelectedFile();
                ImageIO.write(edited, "png", new File(file.getAbsolutePath() + ".png"));

                JOptionPane.showMessageDialog(this, "Saved Successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- RESET ----------------
    private void resetImage() {
        edited = copy(original);
        zoom = 1.0;
        brightness.setValue(0);
        contrast.setValue(0);
        saturation.setValue(0);
        structure.setValue(0);
        ambience.setValue(0);
        whitePoint.setValue(0);
        highlights.setValue(0);
        shadows.setValue(0);
        warmth.setValue(0);
        apply();
    }

    //-----------------UNDO-------------------------
    private void undoLastChange() {
        if (undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo!");
            return;
        }
        edited = undoStack.pop();
        apply();
    }

    // ---------------- APPLY ----------------
    private void apply() {
        if (original == null) return;

        BufferedImage img = copy(edited);

        img = adjust(img,
                brightness.getValue(),
                contrast.getValue(),
                saturation.getValue(),
                structure.getValue(),
                ambience.getValue(),
                whitePoint.getValue(),
                highlights.getValue(),
                shadows.getValue(),
                warmth.getValue()

        );

        int w = (int)(img.getWidth() * zoom);
        int h = (int)(img.getHeight() * zoom);

        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();

        label.setIcon(new ImageIcon(out));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
    }

    // ---------------- FILTERS ----------------
    private BufferedImage toGray(BufferedImage img) {
        for (int y=0;y<img.getHeight();y++)
            for (int x=0;x<img.getWidth();x++) {
                Color c=new Color(img.getRGB(x,y));
                int g=(c.getRed()+c.getGreen()+c.getBlue())/3;
                img.setRGB(x,y,new Color(g,g,g).getRGB());
            }
        return img;
    }

    private BufferedImage toSepia(BufferedImage img) {
        for (int y=0;y<img.getHeight();y++)
            for (int x=0;x<img.getWidth();x++) {
                Color c=new Color(img.getRGB(x,y));

                int r=(int)(0.393*c.getRed()+0.769*c.getGreen()+0.189*c.getBlue());
                int g=(int)(0.349*c.getRed()+0.686*c.getGreen()+0.168*c.getBlue());
                int b=(int)(0.272*c.getRed()+0.534*c.getGreen()+0.131*c.getBlue());

                img.setRGB(x,y,new Color(clamp(r),clamp(g),clamp(b)).getRGB());
            }
        return img;
    }

    private BufferedImage toInvert(BufferedImage img) {
        for (int y=0;y<img.getHeight();y++)
            for (int x=0;x<img.getWidth();x++) {
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        255-c.getRed(),
                        255-c.getGreen(),
                        255-c.getBlue()
                ).getRGB());
            }
        return img;
    }

    private BufferedImage toBlur(BufferedImage img) {
        float[] k={1f/9,1f/9,1f/9,1f/9,1f/9,1f/9,1f/9,1f/9,1f/9};
        return kernel(img,k);
    }

    private BufferedImage toSharpen(BufferedImage img) {
        float[] k={0,-1,0,-1,5,-1,0,-1,0};
        return kernel(img,k);
    }

    private BufferedImage kernel(BufferedImage img,float[] k){
        BufferedImage out=new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        ConvolveOp op=new ConvolveOp(new Kernel(3,3,k));
        op.filter(img,out);
        return out;
    }

    // ---------------- EFFECTS ----------------
    private BufferedImage aiEnhance(BufferedImage img){
        for(int y=0;y<img.getHeight();y++)
            for(int x=0;x<img.getWidth();x++){
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        clamp((int)(c.getRed()*1.15)+10),
                        clamp((int)(c.getGreen()*1.15)+10),
                        clamp((int)(c.getBlue()*1.10)+10)
                ).getRGB());
            }
        return img;
    }

    private BufferedImage glow(BufferedImage img){
        for(int y=0;y<img.getHeight();y++)
            for(int x=0;x<img.getWidth();x++){
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        clamp(c.getRed()+50),
                        clamp(c.getGreen()+50),
                        clamp(c.getBlue()+50)
                ).getRGB());
            }
        return img;
    }

    private BufferedImage vintage(BufferedImage img){
        for(int y=0;y<img.getHeight();y++)
            for(int x=0;x<img.getWidth();x++){
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        clamp((int)(c.getRed()*1.3)),
                        clamp((int)(c.getGreen()*0.8)),
                        clamp((int)(c.getBlue()*0.6))
                ).getRGB());
            }
        return img;
    }

    private BufferedImage hdr(BufferedImage img){
        for(int y=0;y<img.getHeight();y++)
            for(int x=0;x<img.getWidth();x++){
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        clamp((int)(Math.pow(c.getRed()/255.0,0.7)*255)),
                        clamp((int)(Math.pow(c.getGreen()/255.0,0.7)*255)),
                        clamp((int)(Math.pow(c.getBlue()/255.0,0.7)*255))
                ).getRGB());
            }
        return img;
    }

    private BufferedImage softLight(BufferedImage img){
        for(int y=0;y<img.getHeight();y++)
            for(int x=0;x<img.getWidth();x++){
                Color c=new Color(img.getRGB(x,y));
                img.setRGB(x,y,new Color(
                        clamp((c.getRed()+255)/2),
                        clamp((c.getGreen()+255)/2),
                        clamp((c.getBlue()+255)/2)
                ).getRGB());
            }
        return img;
    }

    // ---------------- ADJUST ----------------
    private BufferedImage adjust(BufferedImage img, int br, int con, int sat,
                                 int structure, int ambience, int whitePoint,
                                 int highlights, int shadows, int warmth) {
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));

                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();

                // BRIGHTNESS
                r += br; g += br; b += br;

                // CONTRAST
                double f = (259.0 * (con + 255)) / (255.0 * (259 - con));
                r = (int)(f * (r - 128) + 128);
                g = (int)(f * (g - 128) + 128);
                b = (int)(f * (b - 128) + 128);

                // SATURATION
                double avg = (r + g + b) / 3.0;
                double sf = (sat + 100) / 100.0;
                r = (int)(avg + sf * (r - avg));
                g = (int)(avg + sf * (g - avg));
                b = (int)(avg + sf * (b - avg));

                // STRUCTURE (local contrast / edge sharpness)
                double luma = 0.299*r + 0.587*g + 0.114*b;
                double structFactor = structure / 500.0;
                r = (int)(r + structFactor * (r - luma));
                g = (int)(g + structFactor * (g - luma));
                b = (int)(b + structFactor * (b - luma));

                // AMBIENCE (lift shadows + tint toward ambient light)
                double ambFactor = ambience / 500.0;
                r = (int)(r + ambFactor * (128 - r));
                g = (int)(g + ambFactor * (128 - g));
                b = (int)(b + ambFactor * (128 - b));

                // WHITE POINT (shift the white level)
                double wpFactor = 1.0 + whitePoint / 200.0;
                r = (int)(r * wpFactor);
                g = (int)(g * wpFactor);
                b = (int)(b * wpFactor);

                // HIGHLIGHTS (affect bright areas only)
                double brightness = (r + g + b) / 3.0;
                if (brightness > 128) {
                    double hlFactor = highlights / 300.0;
                    r = (int)(r + hlFactor * (255 - r));
                    g = (int)(g + hlFactor * (255 - g));
                    b = (int)(b + hlFactor * (255 - b));
                }

                // SHADOWS (affect dark areas only)
                if (brightness <= 128) {
                    double shFactor = shadows / 300.0;
                    r = (int)(r + shFactor * (128 - r));
                    g = (int)(g + shFactor * (128 - g));
                    b = (int)(b + shFactor * (128 - b));
                }

                // WARMTH (shift toward warm/cool tones)
                double wFactor = warmth / 200.0;
                r = (int)(r + wFactor * 20);
                b = (int)(b - wFactor * 20);

                img.setRGB(x, y, new Color(clamp(r), clamp(g), clamp(b)).getRGB());
            }
        return img;
    }

    private int clamp(int v){
        return Math.max(0,Math.min(255,v));
    }

    //-------------CROP-----------------------

    private void performCrop(){
        if(zoom != 1.0){

            JOptionPane.showMessageDialog(
                    this,
                    "Reset zoom before cropping");

            return;
        }

        try{

            int x =
                    Math.min(
                            cropStart.x,
                            cropEnd.x);

            int y =
                    Math.min(
                            cropStart.y,
                            cropEnd.y);

            int w =
                    Math.abs(
                            cropStart.x -
                                    cropEnd.x);

            int h =
                    Math.abs(
                            cropStart.y -
                                    cropEnd.y);

            if(w < 10 || h < 10)
                return;

            undoStack.push(
                    copy(edited));

            BufferedImage cropped =
                    edited.getSubimage(
                            x,
                            y,
                            w,
                            h);

            edited = copy(cropped);

            cropMode = false;

            cropStart = null;
            cropEnd = null;

            apply();

        }catch(Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Crop outside image area");
        }
    }

    //----------------MIRROR IMAGE---------------------


    private BufferedImage mirrorHorizontal(
            BufferedImage img){

        int w=img.getWidth();
        int h=img.getHeight();

        BufferedImage out=
                new BufferedImage(
                        w,h,
                        BufferedImage.TYPE_INT_ARGB);

        for(int y=0;y<h;y++)
            for(int x=0;x<w;x++)
                out.setRGB(
                        w-x-1,
                        y,
                        img.getRGB(x,y));

        return out;
    }

    private BufferedImage mirrorVertical(
            BufferedImage img){

        int w=img.getWidth();
        int h=img.getHeight();

        BufferedImage out=
                new BufferedImage(
                        w,h,
                        BufferedImage.TYPE_INT_ARGB);

        for(int y=0;y<h;y++)
            for(int x=0;x<w;x++)
                out.setRGB(
                        x,
                        h-y-1,
                        img.getRGB(x,y));

        return out;
    }

    //-------------------ACTION------------------------

    private BufferedImage denoise(
            BufferedImage img){

        float[] k = {

                1f/16,2f/16,1f/16,
                2f/16,4f/16,2f/16,
                1f/16,2f/16,1f/16

        };

        return kernel(img,k);
    }

    private BufferedImage unblur(
            BufferedImage img){

        float[] k = {

                0,-1,0,
                -1,9,-1,
                0,-1,0

        };

        return kernel(img,k);
    }

    private BufferedImage detailPop(
            BufferedImage img){

        img = unblur(img);

        img = adjust(img, 0, 40, 0,0,0,0,0,0,0);

        return img;
    }

    //--------------BLUR--------------------
    private BufferedImage portraitBlur(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();

        // blur the entire image first
        BufferedImage blurred = toBlur(copy(img));
        blurred = toBlur(blurred); // double blur for stronger effect
        blurred = toBlur(blurred);

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // center oval zone stays sharp
        int cx = w / 2;
        int cy = h / 2;
        int rx = w / 4; // horizontal radius
        int ry = h / 4; // vertical radius

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // check if pixel is inside the center oval
                double dx = (double)(x - cx) / rx;
                double dy = (double)(y - cy) / ry;
                double dist = dx * dx + dy * dy;

                if (dist <= 1.0) {
                    // inside oval — use sharp original
                    out.setRGB(x, y, img.getRGB(x, y));
                } else {
                    // outside oval — use blurred
                    out.setRGB(x, y, blurred.getRGB(x, y));
                }
            }
        }

        return out;
    }

    //-------------------------ROTATE-----------------------------

    private BufferedImage rotate(BufferedImage img, int degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int newW = (int) Math.round(img.getWidth() * cos + img.getHeight() * sin);
        int newH = (int) Math.round(img.getWidth() * sin + img.getHeight() * cos);

        BufferedImage out = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();

        g.translate((newW - img.getWidth()) / 2, (newH - img.getHeight()) / 2);
        g.rotate(radians, img.getWidth() / 2.0, img.getHeight() / 2.0);
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return out;
    }

    private BufferedImage copy(BufferedImage img){
        BufferedImage c=new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics g=c.getGraphics();
        g.drawImage(img,0,0,null);
        g.dispose();
        return c;
    }

    public static void main(String[] args){
        new AdvancedImageEditor();
    }
}
