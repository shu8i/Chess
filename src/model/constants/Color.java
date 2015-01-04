package model.constants;

/**
 * Created by Shahab Shekari on 12/24/14.
 */
public enum Color {

    WHITE("w"),
    BLACK("b"),
    BOTH("x");

    private String abr;

    private Color(String abr)
    {
        this.abr = abr;
    }

    @Override
    public String toString()
    {
        return abr;
    }

}
