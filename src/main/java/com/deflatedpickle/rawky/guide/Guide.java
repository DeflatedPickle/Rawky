package com.deflatedpickle.rawky.guide;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class Guide {
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    @NotNull
    public static String name = "Unnamed";
    public static Icon icon = null;

    @NotNull
    public static List<Class<? extends Guide>> list = new ArrayList<>();

    static {
        Reflections reflections = new Reflections("com.deflatedpickle.rawky.guide");

        for (Class i : reflections.getSubTypesOf(Guide.class)) {
            if (!Modifier.isAbstract(i.getModifiers())) {
                list.add(i);
            }
        }
    }

    @NotNull
    public BasicStroke stroke = new BasicStroke(4f);
    @NotNull
    public Color colour = Color.RED;

    @NotNull
    public Orientation orientation;
    @NotNull
    public Point position;
    @NotNull
    public Dimension parentSize;

    @NotNull
    public List<Rectangle> rectangleList = new ArrayList<>();

    Guide(@NotNull Orientation orientation, @NotNull Point position, @NotNull Dimension parentSize) {
        this.orientation = orientation;
        this.position = position;
        this.parentSize = parentSize;
    }

    public void render(Graphics2D g2D) {
        for (Rectangle rectangle : this.rectangleList) {
            g2D.draw(rectangle);
        }
    }
}
