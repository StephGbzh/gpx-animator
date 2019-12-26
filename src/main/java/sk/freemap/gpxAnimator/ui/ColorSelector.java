/*
 *  Copyright 2013 Martin Ždila, Freemap Slovakia
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package sk.freemap.gpxAnimator.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ColorSelector extends JPanel {

    private static final long serialVersionUID = 6506364764640471311L;

    private final transient JTextField colorTextField;

    private final transient JButton selectButton;


    /**
     * Create the panel.
     */
    public ColorSelector() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        colorTextField = new JTextField();
        colorTextField.setEditable(false);
        colorTextField.setMaximumSize(new Dimension(2147483647, 21));
        colorTextField.setPreferredSize(new Dimension(55, 21));
        add(colorTextField);
        colorTextField.setColumns(10);

        final Component rigidArea = Box.createRigidArea(new Dimension(5, 0));
        add(rigidArea);

        selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JColorChooser chooserPane = new JColorChooser();
                chooserPane.setColor(colorTextField.getBackground());
                final ActionListener okListener = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        setColor(chooserPane.getColor());
                    }
                };
                final JDialog colorChooser = JColorChooser.createDialog(ColorSelector.this, "Track Color", true, chooserPane, okListener, null);
                colorChooser.setVisible(true);
            }
        });

        add(selectButton);
    }

    public Color getColor() {
        return colorTextField.getBackground();
    }

    public void setColor(final Color color) {
        final Color oldColor = colorTextField.getBackground();
        colorTextField.setBackground(color);
        final double l = color.getRed() / 255.0 * 0.299 + color.getGreen() / 255.0 * 0.587 + color.getBlue() / 255.0 * 0.114;
        colorTextField.setForeground(l > 0.5 ? Color.BLACK : Color.WHITE);
        colorTextField.setText("#" + Integer.toHexString(color.getRGB()).toUpperCase());
        firePropertyChange("color", oldColor, color);
    }

    @Override
    public String getToolTipText() {
        return colorTextField.getToolTipText();
    }

    @Override
    public void setToolTipText(final String text) {
        colorTextField.setToolTipText(text);
        selectButton.setToolTipText(text);
    }

}
