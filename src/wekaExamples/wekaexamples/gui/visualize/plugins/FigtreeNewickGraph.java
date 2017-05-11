/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * FigtreeNewickGraph.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package wekaExamples.wekaexamples.gui.visualize.plugins;

import figtree.application.FigTreeFrame;
import weka.gui.visualize.plugins.TreeVisualizePlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

/**
 * Example for displaying a graph in
 * <a href="http://en.wikipedia.org/wiki/Newick_format" target="_blank">Newick format</a>
 * generated by Weka's HierarchicalClusterer with
 * <a href="http://tree.bio.ed.ac.uk/software/figtree/" target="_blank">FigTree</a>.
 * <p/>
 * <b>Note:</b> the leaves must have unique labels (normally the last attribute
 * in the dataset), otherwise an error message about duplicate taxons will
 * pop up.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6043 $
 */
public class FigtreeNewickGraph
        implements Serializable, TreeVisualizePlugin {

    /**
     * for serialization.
     */
    private static final long serialVersionUID = -2495407885027981487L;

    /**
     * Get a JMenu or JMenuItem which contain action listeners
     * that perform the visualization of the graph in newick format.
     * Exceptions thrown because of changes in Weka since compilation need to
     * be caught by the implementer.
     *
     * @param newick the graph in newick format
     * @param name   the name of the item (in the Explorer's history list)
     * @return menuitem    for opening visualization(s), or null
     * to indicate no visualization is applicable for the input
     * @see NoClassDefFoundError
     * @see IncompatibleClassChangeError
     */
    public JMenuItem getVisualizeMenuItem(String newick, String name) {
        JMenuItem result;

        final String newickF = newick;
        final String nameF = name;
        result = new JMenuItem("FigTree graph");
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display(newickF, nameF);
            }
        });

        return result;
    }

    /**
     * Displays the graph.
     *
     * @param newick the graph in newick format
     * @param name   the name of the graph
     */
    protected void display(String newick, String name) {
        FigTreeFrame frame;

        // create frame
        frame = new FigTreeFrame("FigTree graph [" + name + "]");
        frame.initializeComponents();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // parse and display graph
        try {
            frame.readFromString(newick);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Error displaying graph in Newick format: " + e,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the minimum version of Weka, inclusive, the class
     * is designed to work with.  eg: <code>3.5.0</code>
     *
     * @return the minimum version
     */
    public String getMinVersion() {
        return "3.7.1";
    }

    /**
     * Get the maximum version of Weka, exclusive, the class
     * is designed to work with.  eg: <code>3.6.0</code>
     *
     * @return the maximum version
     */
    public String getMaxVersion() {
        return "3.8.0";
    }

    /**
     * Get the specific version of Weka the class is designed for.
     * eg: <code>3.5.1</code>
     *
     * @return the version the plugin was designed for
     */
    public String getDesignVersion() {
        return "3.7.1";
    }
}
