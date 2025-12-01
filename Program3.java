public class Program3 {

    // prefix sums
    private void computePFS(int[] cities, int[] PFS) {
        PFS[0] = cities[0];
        for (int i = 1; i < PFS.length; i++) {
            PFS[i] = PFS[i - 1] + cities[i];
        }
    }

    // sum of interval [l, r]
    private int sum(int[] PFS, int l, int r) {
        //error checking in case wrong indices are passed in
        if (l > r) {
            return 0;
        }

        //subtract left sum s.t. we only have the sum from l -> r
        int left = 0;
        if(l > 0) {
            left = PFS[l - 1]; //subtracting l - 1 removes everything before index l (keeping l)
        }

        return PFS[r] - left;
    }

    // compare left vs right at index k
    private int compare(int[] PFS, int[][] DP, int i, int j, int k) {
        int left  = sum(PFS, i, k); //sum left interval (i,k)
        int right = sum(PFS, k+1, j); //sum right interval (k+1, j)

        int collected = 0;
        int remain = 0;

        //If sum of left < right, enemy destroys right and we keep left
        if (left < right) {
            collected = left;
            remain = DP[i][k];
        }

        //If sum of left > right, enemy destroys left and we keep right
        else if (left > right){
            collected = right;
            remain = DP[k+1][j];
        }

        //If the sume are equal, the enemy will destroy the side that will leave us with the least DP in the long run
        else if (left == right) {
            collected = left; // equal to right
            remain = Math.min(DP[i][k], DP[k+1][j]);
        }

        return collected + remain;
    }

    public int maxCoinValue(int[] cities) {

        //Create index-based sum table (makes all sum computations O(1))
        int[] PFS = new int[cities.length];
        computePFS(cities, PFS);

        //Instantiate final DP table: All indexes are initialized to 0, so for DP[i][i] (all intervals of length 1) are already set to 0
        int[][] DP = new int[cities.length][cities.length];

        //Iterate through all lengths of intervals starting from length of 2 (length 1 will all be DP 0) -> O(n)
        for (int length = 2; length <= cities.length; length++) {

            //Iterate through the length of the entire interval for each length of interval -> O(n)
            for (int i = 0; i + length - 1 < cities.length; i++) {
                int j = i + length - 1;

                // Binary search for best k in [i, j-1]
                int low = i;
                int high = j - 1; //only go until j - 1 bc we cant built a wall after j (the last city -> because then all cities are on one side of wall)

                //Iterate until we have found the peak k value that gives us the most equal sums on either side
                while (low < high) { //in each subinterval, iterate through possible ks to find best k (of all possible subvalues in interval iterate through binary search -> O(log(n))
                    int mid = (low + high) / 2;

                    //Find if sum of left side of mid is greater than or less than right side of mid
                    int v1 = compare(PFS, DP, i, j, mid);
                    int v2 = compare(PFS, DP, i, j, mid+1);

                    //Left < Right -> go right until left >= right
                    if (v1 < v2) {
                        low = mid + 1;
                    }

                    //Left > Right -> go left until we have the tightest bound
                    else if (v1 >= v2) {
                        high = mid;      // slope going down → move left
                    }
                }

                int kStar = low; //low and high should be equal at this point
                int best = 0; //to save the k that gives you left and right sum that are most equal

                // evaluate k* and k*-1
                for (int k = kStar - 1; k <= kStar; k++) {
                    if (k < i || k >= j) continue; //make sure k we found is within i -> j range

                    int left  = sum(PFS, i, k); //find sum to the left of k
                    int right = sum(PFS, k + 1, j); //find sum to the right of k

                    int collected = Math.min(left, right); //we keep the minimum of the sides (enemy destroys higher side)
                    int remain = 0;
                    if (left < right) {
                        remain = DP[i][k];
                    } else if (left > right) {
                        remain = DP[k+1][j];
                    } else {
                        remain = Math.min(DP[i][k], DP[k+1][j]);
                    }

                    best = Math.max(best, collected + remain);
                }

                DP[i][j] = best;
            }
        }

        return DP[0][cities.length - 1];
    }
}
