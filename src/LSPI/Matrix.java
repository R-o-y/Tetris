package LSPI;

import Jama.Matrix;

class MatrixManager {

    /**
     * Performs matrix multiplication.
     * @param A input matrix
     * @param B input matrix
     * @return the new matrix resultMatrix
     */
    public static double [][] matrixMulti(double [][] A, double [][] B){
        return (new Matrix(A)).times(new Matrix(B)).getArrayCopy();
    }

    /**
     * returns the transpose of the input matrix M
     */
    public static double [][] transpose(double [][] M){
        return (new Matrix(M)).transpose().getArrayCopy();
    }

    //Matrix addition. A add B
    public static double[][] matrixAdd(double[][] A, double[][] B) {
        return (new Matrix(A)).plus(new Matrix(B)).getArrayCopy();
    }

    //Matrix substitution. A minus B
    public static double[][] matrixSub(double[][] A, double[][] B) {
        return (new Matrix(A)).minus(new Matrix(B)).getArrayCopy();
    }

    /**
     * Calculate the determinant of the input matrix
     * @param M input matrix
     * @return the determinant
     * @throws IllegalArgumentException
     */
    public static double determinant(double [][] M) throws IllegalArgumentException {
        return (new Matrix(M)).det();
    }

    /**
     * Add constant c to every element in the matrix M
     */
    public static double[][] multiplyByConstant(double[][] M, double c) {
        return (new Matrix(M)).times(c).getArrayCopy();
    }

    /**
     * Return the Inverse of the matrix
     */
    public static double [][] matrixInverse(double [][] M) throws IllegalArgumentException {
        double det = determinant(M);
        if (det == 0){
            throw new IllegalArgumentException("The determinant is Zero, the matrix doesn't have an inverse");
        }
        return (new Matrix(M)).inverse().getArrayCopy();
    }

    public static double [][] convertToColumnVector(double[] singleArray){
        double[][] columnVector = new double[singleArray.length][1];
        for(int i=0;i<singleArray.length;i++) {
            columnVector[i][0]=singleArray[i];
        }
        return columnVector;
    }

    public static double[] convertToArray(double[][] myMatrix){
        double[] myArray = new double[myMatrix.length];
        for(int i=0;i<myMatrix.length;i++) {
            myArray[i]=myMatrix[i][0];
        }
        return myArray;
    }

}
