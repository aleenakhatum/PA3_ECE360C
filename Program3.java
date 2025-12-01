public class Program3 {

    public void computePFS(int[] cities, int[] PFS) {
        PFS[0] = cities[0];
        for (int i = 1; i < PFS.length; i++) {
            PFS[i] = PFS[i - 1] + cities[i];
        }
    }

    public int maxCoinValue(int[] cities) {
        // Implement your dynamic programming algorithm here
        // You may use helper functions as needed
        int[] PFS = new int[cities.length];   // creates an array of length of input cities
        computePFS(cities, PFS);
        return 0;
    }
}
