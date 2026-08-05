package com.cscreativ.billboard.creative.domain.valueobject;

import java.util.Objects;

public class ProofDimensions {
    private final int widthInPixels;
    private final int heightInPixels;

    public ProofDimensions(int widthInPixels, int heightInPixels) {
        if (widthInPixels <= 0 || heightInPixels <= 0) {
            throw new IllegalArgumentException("Les dimensions doivent être supérieures à zéro");
        }
        this.widthInPixels = widthInPixels;
        this.heightInPixels = heightInPixels;
    }

    public int getWidthInPixels() { return widthInPixels; }
    public int getHeightInPixels() { return heightInPixels; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofDimensions dimensions = (ProofDimensions) o;
        return widthInPixels == dimensions.widthInPixels && heightInPixels == dimensions.heightInPixels;
    }

    @Override
    public int hashCode() {
        return Objects.hash(widthInPixels, heightInPixels);
    }
}
