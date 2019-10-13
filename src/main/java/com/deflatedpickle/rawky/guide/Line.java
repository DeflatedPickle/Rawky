package com.deflatedpickle.rawky.guide;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Line extends Guide {
    public static String name = "Line";

    public Line(Orientation orientation, Point position, Dimension size) {
        super(orientation, position, size);

        rectangleList.add(new Rectangle(
                position.x - (int) this.stroke.getLineWidth(),
                position.y,
                (int) this.stroke.getLineWidth() * 2,
                parentSize.height)
        );
    }

    @Override
    public void render(Graphics2D g2D) {
        for (Rectangle rectangle : this.rectangleList) {
            g2D.fill(rectangle);
        }
    }
}
