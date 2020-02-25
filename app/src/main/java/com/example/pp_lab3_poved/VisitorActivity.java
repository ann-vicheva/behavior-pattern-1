package com.example.pp_lab3_poved;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VisitorActivity extends AppCompatActivity {

    //---------------
    public interface Shape {
        void move(int x, int y);
        void draw();
        String accept(Visitor visitor);
    }
    //---------------
    public class Dot implements Shape {
        private int id;
        private int x;
        private int y;

        public Dot() {
        }

        public Dot(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        @Override
        public void move(int x, int y) {
            // move shape
        }

        @Override
        public void draw() {
            // draw shape
        }

        public String accept(Visitor visitor) {
            return visitor.visitDot(this);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getId() {
            return id;
        }
    }
    //---------------
    public class Circle extends Dot {
        private int radius;

        public Circle(int id, int x, int y, int radius) {
            super(id, x, y);
            this.radius = radius;
        }

        @Override
        public String accept(Visitor visitor) {
            return visitor.visitCircle(this);
        }

        public int getRadius() {
            return radius;
        }
    }
    //---------------
    public class Rectangle implements Shape {
        private int id;
        private int x;
        private int y;
        private int width;
        private int height;

        public Rectangle(int id, int x, int y, int width, int height) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String accept(Visitor visitor) {
            return visitor.visitRectangle(this);
        }

        @Override
        public void move(int x, int y) {
            // move shape
        }

        @Override
        public void draw() {
            // draw shape
        }

        public int getId() {
            return id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
    //---------------
    public class CompoundShape implements Shape {
        public int id;
        public List<Shape> children = new ArrayList<>();

        public CompoundShape(int id) {
            this.id = id;
        }

        @Override
        public void move(int x, int y) {
            // move shape
        }

        @Override
        public void draw() {
            // draw shape
        }

        public int getId() {
            return id;
        }

        @Override
        public String accept(Visitor visitor) {
            return visitor.visitCompoundGraphic(this);
        }

        public void add(Shape shape) {
            children.add(shape);
        }
    }
    //---------------
    public interface Visitor {
        String visitDot(Dot dot);

        String visitCircle(Circle circle);

        String visitRectangle(Rectangle rectangle);

        String visitCompoundGraphic(CompoundShape cg);
    }
    //---------------
    public static class XMLExportVisitor implements Visitor {

        public String export(Shape... args) {
            StringBuilder sb = new StringBuilder();
            for (Shape shape : args) {
                //sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
                sb.append(shape.accept(this)).append("\n");
                tv.setText(tv.getText()+"\n"+sb.toString());
                //System.out.println(sb.toString());
                sb.setLength(0);
            }
            return sb.toString();
        }

        public String visitDot(Dot d) {
            return "<dot>" + "\n" +
                    "    <id>" + d.getId() + "</id>" + "\n" +
                    "    <x>" + d.getX() + "</x>" + "\n" +
                    "    <y>" + d.getY() + "</y>" + "\n" +
                    "</dot>";
        }

        public String visitCircle(Circle c) {
            return "<circle>" + "\n" +
                    "    <id>" + c.getId() + "</id>" + "\n" +
                    "    <x>" + c.getX() + "</x>" + "\n" +
                    "    <y>" + c.getY() + "</y>" + "\n" +
                    "    <radius>" + c.getRadius() + "</radius>" + "\n" +
                    "</circle>";
        }

        public String visitRectangle(Rectangle r) {
            return "<rectangle>" + "\n" +
                    "    <id>" + r.getId() + "</id>" + "\n" +
                    "    <x>" + r.getX() + "</x>" + "\n" +
                    "    <y>" + r.getY() + "</y>" + "\n" +
                    "    <width>" + r.getWidth() + "</width>" + "\n" +
                    "    <height>" + r.getHeight() + "</height>" + "\n" +
                    "</rectangle>";
        }

        public String visitCompoundGraphic(CompoundShape cg) {
            return "<compound_graphic>" + "\n" +
                    "   <id>" + cg.getId() + "</id>" + "\n" +
                    _visitCompoundGraphic(cg) +
                    "</compound_graphic>";
        }

        private String _visitCompoundGraphic(CompoundShape cg) {
            StringBuilder sb = new StringBuilder();
            for (Shape shape : cg.children) {
                String obj = shape.accept(this);
                // Proper indentation for sub-objects.
                obj = "    " + obj.replace("\n", "\n    ") + "\n";
                sb.append(obj);
            }
            return sb.toString();
        }

    }
    //---------------
    //---------------
    //---------------

    static TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void create(View view){
        Dot dot = new Dot(1, 10, 55);
        Circle circle = new Circle(2, 23, 15, 10);
        Rectangle rectangle = new Rectangle(3, 10, 17, 20, 30);

        CompoundShape compoundShape = new CompoundShape(4);
        compoundShape.add(dot);
        compoundShape.add(circle);
        compoundShape.add(rectangle);

        CompoundShape c = new CompoundShape(5);
        c.add(dot);
        compoundShape.add(c);

        export(circle, compoundShape);
    }

    private static void export(Shape... shapes) {
        XMLExportVisitor exportVisitor = new XMLExportVisitor();
        tv.setText(tv.getText()+exportVisitor.export(shapes));
        System.out.println(exportVisitor.export(shapes));
    }

    public void clear(View view){
        tv.setText("");
    }
}
