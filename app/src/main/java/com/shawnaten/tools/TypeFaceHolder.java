package com.shawnaten.tools;

import android.graphics.Typeface;

/**
 * Created by Shawn Aten on 8/15/14.
 */
public class TypeFaceHolder {
    private int family, style;
    private boolean italicized;
    private Typeface typeface;

    public TypeFaceHolder(int family, int style, boolean italicized) {
        this.family = family;
        this.style = style;
        this.italicized = italicized;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    @Override
    public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof TypeFaceHolder)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        TypeFaceHolder lhs = (TypeFaceHolder) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return family == lhs.family && style == lhs.style && italicized == lhs.italicized;
    }
}
