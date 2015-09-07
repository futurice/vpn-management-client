package com.futurice.intra.vpn.vendor;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-07.
 *
 * Copy from: https://bitbucket.org/controlsfx/controlsfx/issues/84/segmented-button-control-clicking-on
 */
public class SegmentableButton extends ToggleButton {
    @Override
    public void fire() {
        // we don't toggle from selected to not selected if part of a group
        if (getToggleGroup() == null || !isSelected()) {
            super.fire();
        }
    }
    public SegmentableButton() {
        super();
    }
    public SegmentableButton(String text, Node graphic) {
        super(text, graphic);
    }
    public SegmentableButton(String text) {
        super(text);
    }
}