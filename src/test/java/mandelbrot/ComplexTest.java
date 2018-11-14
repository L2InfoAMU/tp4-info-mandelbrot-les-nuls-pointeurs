package mandelbrot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComplexTest {
    private final Complex onePlusI = new Complex(1,1);
    private final Complex minusI = new Complex(0,-1);
    private final Complex minusOne = new Complex(-1,0);
    private final Complex oneMinusI = new Complex(1, -1);
    private final Complex twoI = new Complex(0,2);
    private final Complex two = new Complex(2,0);
    private final double real = -12;
    private final double imaginary = 10;


    @Test
    void testConstructor(){
        assertEquals(0., twoI.real, Helpers.EPSILON);
        assertEquals(2., twoI.imaginary, Helpers.EPSILON);
        assertEquals(1., oneMinusI.real, Helpers.EPSILON);
        assertEquals(-1., oneMinusI.imaginary, Helpers.EPSILON);
        assertEquals(2., two.real, Helpers.EPSILON);
        assertEquals(0., two.imaginary, Helpers.EPSILON);
    }

    @Test
    void testGetReal(){
        assertEquals(2., two.getReal(), Helpers.EPSILON);
        assertEquals(1., oneMinusI.getReal(), Helpers.EPSILON);
        assertEquals(-1., new Complex(-1,1).getReal(), Helpers.EPSILON);
        assertEquals(real, new Complex(real, imaginary).getReal(), Helpers.EPSILON);
    }

    @Test
    void testGetImaginary(){
        assertEquals(2., twoI.getImaginary(), Helpers.EPSILON);
        assertEquals(1., new Complex(-1, 1).getImaginary(), Helpers.EPSILON);
        assertEquals(-1., oneMinusI.getImaginary(), Helpers.EPSILON);
        assertEquals(imaginary, new Complex(real, imaginary).getImaginary(), Helpers.EPSILON);
    }

    @Test
    void testOne(){
        assertEquals(1., Complex.ONE.getReal());
        assertEquals(0., Complex.ONE.getImaginary());
    }

    @Test
    void testI(){
        assertEquals(0, Complex.I.getReal());
        assertEquals(1, Complex.I.getImaginary());
    }

    @Test
    void testZero(){
        assertEquals(0, Complex.ZERO.getReal());
        assertEquals(0, Complex.ZERO.getImaginary());
    }

    @Test
    void testNegate(){
        assertEquals(minusOne, Complex.ONE.negate());
        assertEquals(Complex.I, minusI.negate());
        assertEquals(new Complex(-1, 1), oneMinusI.negate());
        assertEquals(new Complex(real, imaginary), new Complex(-real,-imaginary).negate());
    }

    @Test
    void testReciprocal(){
        assertEquals(Complex.ONE, Complex.ONE.reciprocal());
        assertEquals(Complex.I, minusI.reciprocal());
        assertEquals(new Complex(0.5,0), two.reciprocal());
        assertEquals(new Complex(0.5,0.5), oneMinusI.reciprocal());
    }

    @Test
    void testReciprocalOfZero(){
        assertThrows(ArithmeticException.class, ()->Complex.ZERO.reciprocal());
    }

    @Test
    void testSubstract(){
        assertEquals(minusOne, Complex.ZERO.subtract(Complex.ONE));
        assertEquals(oneMinusI, Complex.ONE.subtract(Complex.I));
        assertEquals(new Complex(real-1,imaginary-1),
                new Complex(real, imaginary).subtract(onePlusI));
    }

    @Test
    void testDivide(){
        assertEquals(onePlusI, onePlusI.divide(Complex.ONE));
        assertEquals(new Complex(0.5, 0), Complex.ONE.divide(two));
        assertEquals(minusI,oneMinusI.divide(onePlusI));
        assertThrows(ArithmeticException.class, ()->Complex.ONE.divide(Complex.ZERO));
    }

    @Test
    void testDivideByZero(){
        assertThrows(ArithmeticException.class, ()->Complex.ONE.divide(Complex.ZERO));
    }

    @Test
    void testConjugate(){
        assertEquals(Complex.ZERO, Complex.ZERO.conjugate());
        assertEquals(Complex.ONE, Complex.ONE.conjugate());
        assertEquals(onePlusI, oneMinusI.conjugate());
        assertEquals(new Complex(real, -imaginary), new Complex(real, imaginary).conjugate());
    }

    @Test
    void testRotation(){
        assertEquals(Complex.I, Complex.rotation(Math.PI/2));
        assertEquals(minusI, Complex.rotation(-Math.PI/2));
        assertEquals(Complex.ONE, Complex.rotation(0));
        assertEquals(new Complex(Math.sqrt(2)/2., Math.sqrt(2)/2.),
                Complex.rotation(Math.PI/4));
        assertEquals(new Complex(1./2., Math.sqrt(3)/2.),
                Complex.rotation(Math.PI/3));
    }

    @Test
    void testToString(){
        assertEquals("Complex{real=1.0, imaginary=-1.0}", oneMinusI.toString());
        assertEquals("Complex{real="+real+", imaginary="+imaginary+"}", new Complex(real, imaginary).toString());
    }

    @Test
    void testHashCode() {
        Complex c1 = new Complex(real, imaginary);
        Complex c2 = new Complex(real, imaginary);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testReal(){
        assertEquals(two,Complex.real(2));
        assertEquals(Complex.ZERO,Complex.real(0));
        assertEquals(5,Complex.real(5).getReal());
        assertEquals(0,Complex.real(5).getImaginary());
    }

    @Test
    void testAdd(){
        assertEquals(Complex.ONE,Complex.ZERO.add(Complex.ONE));
        assertEquals(Complex.ZERO,Complex.ZERO.add(Complex.ZERO));
        assertEquals(minusI,Complex.ZERO.add(new Complex(0,-1)));
        assertEquals(new Complex(2,2),two.add(twoI));
    }

    @Test
    void testMultiply(){
        assertEquals(Complex.ONE,Complex.ONE.multiply(Complex.ONE));
        assertEquals(Complex.ZERO,Complex.ZERO.multiply(Complex.ZERO));
        assertEquals(Complex.real(-1),Complex.I.multiply(Complex.I));
        assertEquals(new Complex(44,-240), new Complex(real,imaginary).multiply(new Complex(real,imaginary)));
    }

    @Test
    void testSquareModulus() {
        assertEquals(1,Complex.I.squaredModulus());
        assertEquals(1,Complex.ONE.squaredModulus());
        assertEquals(0,Complex.ZERO.squaredModulus());
        assertEquals(1,Complex.ONE.rotation(3).squaredModulus(),0.000000000001);
        assertEquals(4,two.squaredModulus());
        assertEquals(4,twoI.squaredModulus());
        assertEquals(8,two.add(twoI).squaredModulus());
    }

    @Test
    void testModulus(){
        assertEquals(1,Complex.I.modulus());
        assertEquals(1,Complex.ONE.modulus());
        assertEquals(0,Complex.ZERO.modulus());
        assertEquals(0,Helpers.doubleCompare(Complex.ONE.rotation(3).modulus(), 1.0));
        assertEquals(2,two.modulus());
        assertEquals(2,twoI.modulus());
        assertEquals(2*Math.sqrt(2),two.add(twoI).modulus());
    }

    @Test
    void testPow() {
        assertEquals(Complex.ONE, Complex.I.pow(4));
        assertEquals(two,Complex.real(Math.sqrt(2)).pow(2));
        assertEquals(two.pow(2),new Complex(0,Math.sqrt(2)).pow(4));
        assertEquals(Complex.I, Complex.I.pow(13));
        assertEquals(Complex.real(Math.pow(3,14)),Complex.real(3).pow(14));
    }

    @Test
    void testScale(){
        assertEquals(two, Complex.ONE.scale(2));
        assertEquals(twoI, Complex.I.scale(2));
        assertEquals(Complex.ZERO,Complex.ONE.scale(0));
        assertEquals(Complex.ZERO,Complex.ZERO.scale(42));
        assertEquals(two.add(twoI),Complex.ONE.add(Complex.I).scale(2));
    }

    @Test
    void testEquals(){
        assertEquals(two,Complex.real(2));
        assertEquals(Complex.ZERO,new Complex(0,0));
        assertEquals(Complex.ZERO,Complex.I.subtract(Complex.I));
    }

}