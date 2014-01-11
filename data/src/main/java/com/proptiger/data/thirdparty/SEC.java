package com.proptiger.data.thirdparty;

import org.springframework.stereotype.Controller;

/**
 * SEC.java
 * 
 * The SEC class computes the Smallest Enclosing circle of a set of 2D points.
 * 
 * Created: Aug 16 2006
 * 
 * Author: Xiumin Diao (xiumin@nmsu.edu)
 * 
 */

@Controller
public class SEC {

    // Compute the Smallest Enclosing Circle of the n points in p,
    // such that the m points in B lie on the boundary of the circle.
    public static Circle findSec(int n, Point[] p, int m, Point[] b) {
        Circle sec = new Circle();

        // Compute the Smallest Enclosing Circle defined by B
        if (m == 1) {
            sec = new Circle(b[0]);
        } else if (m == 2) {
            sec = new Circle(b[0], b[1]);
        } else if (m == 3) {
            return new Circle(b[0], b[1], b[2]);
        }

        // Check if all the points in p are enclosed
        for (int i = 0; i < n; i++) {
            if (sec.contain(p[i]) == 1) {
                // Compute B <--- B union P[i].
                b[m] = new Point(p[i]);
                // Recurse
                sec = findSec(i, p, m + 1, b);
            }
        }

        return sec;
    }
}
