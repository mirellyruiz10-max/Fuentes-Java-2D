package editorwordart;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;

public class WordArtEditor extends JFrame {

    private final Color verdeOscuro = new Color(0, 105, 92);
    private final Color verdeEsmeralda = new Color(0, 150, 136);
    private final Color verdeClaro = new Color(224, 242, 241);
    private final Color verdeMuyClaro = new Color(240, 253, 250);
    private final Color textoOscuro = new Color(20, 55, 50);

    private final JTextField txtTexto = new JTextField("WORDART", 18);
    private JComboBox<String> cbFuente;
    private final JSpinner spTamano = new JSpinner(new SpinnerNumberModel(80, 10, 250, 1));
    private final JCheckBox chkNegrita = new JCheckBox("Negrita");
    private final JCheckBox chkCursiva = new JCheckBox("Cursiva");
    private final JCheckBox chkSombra = new JCheckBox("Sombra", true);

    private final JComboBox<String> cbEstilo = new JComboBox<>(new String[]{
        "Sólido", "Degradado", "Solo contorno"
    });

    private final JSlider slContorno = new JSlider(1, 10, 3);
    private final JSlider slPerspectiva = new JSlider(-50, 50, 0);
    private final JSlider slRotacion = new JSlider(-45, 45, 0);
    private final JSlider slSombraX = new JSlider(-30, 30, 8);
    private final JSlider slSombraY = new JSlider(-30, 30, 8);

    private Color colorRelleno = new Color(30, 120, 255);
    private Color colorContorno = Color.BLACK;
    private Color colorSombra = new Color(0, 0, 0, 130);

    private final LienzoWordArt lienzo = new LienzoWordArt();

    public WordArtEditor() {
        setTitle("Editor WordArt 2D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(verdeMuyClaro);

        String[] fuentes = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        cbFuente = new JComboBox<>(fuentes);
        cbFuente.setSelectedItem("Arial");

        add(crearPanelControles(), BorderLayout.WEST);
        add(lienzo, BorderLayout.CENTER);
        conectarEventos();
    }

    private JPanel crearPanelControles() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(verdeClaro);
        contenedor.setPreferredSize(new Dimension(320, 720));
        contenedor.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, verdeEsmeralda));

        JPanel panel = new JPanel();
        panel.setBackground(verdeClaro);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("FORMATO WORDART");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(verdeOscuro);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(8));

        panel.add(etiqueta("Texto:"));
        txtTexto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        txtTexto.setPreferredSize(new Dimension(250, 26));
        txtTexto.setBackground(Color.WHITE);
        txtTexto.setForeground(textoOscuro);
        panel.add(txtTexto);
        panel.add(Box.createVerticalStrut(6));

        panel.add(etiqueta("Fuente:"));
        cbFuente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        cbFuente.setPreferredSize(new Dimension(250, 26));
        cbFuente.setBackground(Color.WHITE);
        cbFuente.setForeground(textoOscuro);
        panel.add(cbFuente);
        panel.add(Box.createVerticalStrut(6));

        panel.add(etiqueta("Tamaño:"));
        spTamano.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        spTamano.setPreferredSize(new Dimension(250, 26));
        spTamano.setBackground(Color.WHITE);
        panel.add(spTamano);
        panel.add(Box.createVerticalStrut(6));

        JPanel estilos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        estilos.setBackground(verdeClaro);
        estilos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        chkNegrita.setBackground(verdeClaro);
        chkCursiva.setBackground(verdeClaro);
        chkSombra.setBackground(verdeClaro);
        chkNegrita.setForeground(textoOscuro);
        chkCursiva.setForeground(textoOscuro);
        chkSombra.setForeground(textoOscuro);
        estilos.add(chkNegrita);
        estilos.add(chkCursiva);
        estilos.add(chkSombra);
        estilos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(estilos);
        panel.add(Box.createVerticalStrut(6));

        panel.add(etiqueta("Estilo:"));
        cbEstilo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        cbEstilo.setPreferredSize(new Dimension(250, 26));
        cbEstilo.setBackground(Color.WHITE);
        cbEstilo.setForeground(textoOscuro);
        panel.add(cbEstilo);
        panel.add(Box.createVerticalStrut(8));

        JButton btnRelleno = new JButton("Color de relleno");
        JButton btnContorno = new JButton("Color de contorno");
        JButton btnSombra = new JButton("Color de sombra");

        configurarBoton(btnRelleno);
        configurarBoton(btnContorno);
        configurarBoton(btnSombra);

        btnRelleno.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Color del texto", colorRelleno);
            if (nuevo != null) {
                colorRelleno = nuevo;
                lienzo.repaint();
            }
        });

        btnContorno.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Color del contorno", colorContorno);
            if (nuevo != null) {
                colorContorno = nuevo;
                lienzo.repaint();
            }
        });

        btnSombra.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Color de sombra", colorSombra);
            if (nuevo != null) {
                colorSombra = new Color(nuevo.getRed(), nuevo.getGreen(), nuevo.getBlue(), 140);
                lienzo.repaint();
            }
        });

        panel.add(btnRelleno);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnContorno);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnSombra);
        panel.add(Box.createVerticalStrut(6));

        panel.add(etiqueta("Grosor del contorno:"));
        configurarSlider(slContorno, 1, 10, 3);
        panel.add(slContorno);

        panel.add(etiqueta("Perspectiva:"));
        configurarSlider(slPerspectiva, -50, 50, 25);
        panel.add(slPerspectiva);

        panel.add(etiqueta("Rotación:"));
        configurarSlider(slRotacion, -45, 45, 15);
        panel.add(slRotacion);

        panel.add(etiqueta("Sombra horizontal:"));
        configurarSlider(slSombraX, -30, 30, 10);
        panel.add(slSombraX);

        panel.add(etiqueta("Sombra vertical:"));
        configurarSlider(slSombraY, -30, 30, 10);
        panel.add(slSombraY);

        JButton btnRestablecer = new JButton("Restablecer");
        configurarBoton(btnRestablecer);
        btnRestablecer.addActionListener(e -> restablecer());

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(verdeClaro);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(4, 10, 8, 10));
        panelInferior.add(btnRestablecer, BorderLayout.CENTER);

        contenedor.add(panel, BorderLayout.CENTER);
        contenedor.add(panelInferior, BorderLayout.SOUTH);
        return contenedor;
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(textoOscuro);
        return label;
    }

    private void configurarBoton(JButton boton) {
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        boton.setBackground(new Color(178, 223, 219));
        boton.setForeground(verdeOscuro);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(verdeEsmeralda, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void configurarSlider(JSlider slider, int minimo, int maximo, int espacio) {
        slider.setMinimum(minimo);
        slider.setMaximum(maximo);
        slider.setMajorTickSpacing(espacio);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        slider.setPreferredSize(new Dimension(250, 44));
        slider.setBackground(verdeClaro);
        slider.setForeground(textoOscuro);
    }

    private void conectarEventos() {
        ActionListener accion = e -> lienzo.repaint();

        cbFuente.addActionListener(accion);
        cbEstilo.addActionListener(accion);
        chkNegrita.addActionListener(accion);
        chkCursiva.addActionListener(accion);
        chkSombra.addActionListener(accion);
        spTamano.addChangeListener(e -> lienzo.repaint());

        ChangeListener cambio = e -> lienzo.repaint();
        slContorno.addChangeListener(cambio);
        slPerspectiva.addChangeListener(cambio);
        slRotacion.addChangeListener(cambio);
        slSombraX.addChangeListener(cambio);
        slSombraY.addChangeListener(cambio);

        txtTexto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lienzo.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lienzo.repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lienzo.repaint();
            }
        });
    }

    private void restablecer() {
        txtTexto.setText("WORDART");
        cbFuente.setSelectedItem("Arial");
        spTamano.setValue(80);
        chkNegrita.setSelected(false);
        chkCursiva.setSelected(false);
        chkSombra.setSelected(true);
        cbEstilo.setSelectedIndex(0);
        slContorno.setValue(3);
        slPerspectiva.setValue(0);
        slRotacion.setValue(0);
        slSombraX.setValue(8);
        slSombraY.setValue(8);
        colorRelleno = new Color(30, 120, 255);
        colorContorno = Color.BLACK;
        colorSombra = new Color(0, 0, 0, 130);
        lienzo.repaint();
    }

    private class LienzoWordArt extends JPanel {

        public LienzoWordArt() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(verdeEsmeralda),
                    "Vista previa",
                    0,
                    0,
                    null,
                    verdeOscuro));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            String texto = txtTexto.getText();
            if (texto == null || texto.trim().isEmpty()) {
                g2.dispose();
                return;
            }

            int estiloFuente = Font.PLAIN;
            if (chkNegrita.isSelected()) estiloFuente |= Font.BOLD;
            if (chkCursiva.isSelected()) estiloFuente |= Font.ITALIC;

            Font fuente = new Font(
                    (String) cbFuente.getSelectedItem(),
                    estiloFuente,
                    (Integer) spTamano.getValue());

            FontRenderContext frc = g2.getFontRenderContext();
            GlyphVector gv = fuente.createGlyphVector(frc, texto);
            Shape forma = gv.getOutline();
            Rectangle2D limites = forma.getBounds2D();

            Insets margen = getInsets();
            double centroX = margen.left + (getWidth() - margen.left - margen.right) / 2.0;
            double centroY = margen.top + (getHeight() - margen.top - margen.bottom) / 2.0;

            AffineTransform centrar = new AffineTransform();
            centrar.translate(
                    centroX - limites.getCenterX(),
                    centroY - limites.getCenterY());

            forma = centrar.createTransformedShape(forma);
            forma = aplicarPerspectiva(
                    forma,
                    slPerspectiva.getValue() / 100.0,
                    centroX,
                    centroY);

            double angulo = Math.toRadians(slRotacion.getValue());
            AffineTransform rotar = AffineTransform.getRotateInstance(
                    angulo, centroX, centroY);
            forma = rotar.createTransformedShape(forma);

            Rectangle2D limitesFinales = forma.getBounds2D();
            AffineTransform ajusteFinal = AffineTransform.getTranslateInstance(
                    centroX - limitesFinales.getCenterX(),
                    centroY - limitesFinales.getCenterY());
            forma = ajusteFinal.createTransformedShape(forma);

            dibujarSombra(g2, forma);
            dibujarRelleno(g2, forma);
            dibujarContorno(g2, forma);

            g2.dispose();
        }

        private void dibujarSombra(Graphics2D g2, Shape forma) {
            if (!chkSombra.isSelected()) return;

            AffineTransform movimiento = AffineTransform.getTranslateInstance(
                    slSombraX.getValue(), slSombraY.getValue());

            Shape sombra = movimiento.createTransformedShape(forma);
            g2.setColor(colorSombra);
            g2.fill(sombra);
        }

        private void dibujarRelleno(Graphics2D g2, Shape forma) {
            String estilo = (String) cbEstilo.getSelectedItem();
            if ("Solo contorno".equals(estilo)) return;

            if ("Degradado".equals(estilo)) {
                Rectangle2D limites = forma.getBounds2D();
                Color claro = colorRelleno.brighter();
                Color oscuro = colorRelleno.darker();

                GradientPaint degradado = new GradientPaint(
                        (float) limites.getX(),
                        (float) limites.getY(),
                        claro,
                        (float) limites.getMaxX(),
                        (float) limites.getMaxY(),
                        oscuro);

                g2.setPaint(degradado);
            } else {
                g2.setColor(colorRelleno);
            }

            g2.fill(forma);
        }

        private void dibujarContorno(Graphics2D g2, Shape forma) {
            g2.setColor(colorContorno);
            g2.setStroke(new BasicStroke(
                    slContorno.getValue(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g2.draw(forma);
        }

        private Shape aplicarPerspectiva(
                Shape original,
                double intensidad,
                double centroX,
                double centroY) {

            if (Math.abs(intensidad) < 0.001) return original;

            Rectangle2D bounds = original.getBounds2D();
            double mitadAncho = Math.max(1, bounds.getWidth() / 2.0);
            PathIterator iterator = original.getPathIterator(null, 0.8);
            Path2D.Double nuevaForma = new Path2D.Double();
            double[] coordenadas = new double[6];

            while (!iterator.isDone()) {
                int tipo = iterator.currentSegment(coordenadas);
                double[] punto;

                switch (tipo) {
                    case PathIterator.SEG_MOVETO:
                        punto = transformarPunto(coordenadas[0], coordenadas[1],
                                intensidad, centroX, centroY, mitadAncho);
                        nuevaForma.moveTo(punto[0], punto[1]);
                        break;

                    case PathIterator.SEG_LINETO:
                        punto = transformarPunto(coordenadas[0], coordenadas[1],
                                intensidad, centroX, centroY, mitadAncho);
                        nuevaForma.lineTo(punto[0], punto[1]);
                        break;

                    case PathIterator.SEG_QUADTO:
                        double[] p1 = transformarPunto(coordenadas[0], coordenadas[1],
                                intensidad, centroX, centroY, mitadAncho);
                        double[] p2 = transformarPunto(coordenadas[2], coordenadas[3],
                                intensidad, centroX, centroY, mitadAncho);
                        nuevaForma.quadTo(p1[0], p1[1], p2[0], p2[1]);
                        break;

                    case PathIterator.SEG_CUBICTO:
                        double[] q1 = transformarPunto(coordenadas[0], coordenadas[1],
                                intensidad, centroX, centroY, mitadAncho);
                        double[] q2 = transformarPunto(coordenadas[2], coordenadas[3],
                                intensidad, centroX, centroY, mitadAncho);
                        double[] q3 = transformarPunto(coordenadas[4], coordenadas[5],
                                intensidad, centroX, centroY, mitadAncho);
                        nuevaForma.curveTo(q1[0], q1[1], q2[0], q2[1], q3[0], q3[1]);
                        break;

                    case PathIterator.SEG_CLOSE:
                        nuevaForma.closePath();
                        break;
                }

                iterator.next();
            }

            return nuevaForma;
        }

        private double[] transformarPunto(
                double x,
                double y,
                double intensidad,
                double centroX,
                double centroY,
                double mitadAncho) {

            double posicionX = (x - centroX) / mitadAncho;
            posicionX = Math.max(-1, Math.min(1, posicionX));

            double escalaY = 1 + intensidad * posicionX * 0.75;
            double nuevaY = centroY + (y - centroY) * escalaY;
            double nuevaX = x + (y - centroY) * intensidad * 0.18;

            return new double[]{nuevaX, nuevaY};
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new WordArtEditor().setVisible(true);
        });
    }
}
