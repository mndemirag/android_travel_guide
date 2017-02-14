package androidapp.uturn.model;

import java.io.*;
import java.io.IOException;


/**
 * Created by Parth on 3/6/2016
 */
public class StraightTrip {

    public static long arr[][] = new long[10][10];
    public static long a[][] = new long[10][10];
    public static int traceroute[] = {1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    public static int nof, z, f1 = 2;
    public static long sum = 0;
    public static int fin[][] = new int[10][2];

    // Function for creating a matrix for filling distance between places
    public static void dist() throws IOException {

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        int i, j;
        int loc1 = 1, loc2 = 0;
        System.out.println("Enter the number of places: ");
        nof= Integer.parseInt(buf.readLine());
        loc2=nof;
        traceroute[loc2]=0;
        ++nof;
        z=nof;
        for(i=0;i<nof;i++)//to assign row number and column number
        {
            arr[z][i]=i;
            arr[i][z]=i;
        }
        System.out.print("Enter 1 for distance and 0 for time");
        String str=buf.readLine();
        int opt= Integer.parseInt(str);
        if(opt==1)
        {
            for(i=1;i<nof;i++)//for entering distance between all the nodes
            {
                for(j=1;j<nof;j++)
                {
                    if(i<j)
                    {
                        System.out.print("Enter distance between "+i+"and "+ j+":");
                        String s=buf.readLine();
                        long m= Long.valueOf(s).longValue();
                        arr[i][j]=m;//original array on which we will perform the algorithm
                        arr[j][i]=m;
                        a[i][j]=m;//duplicating the original array for back up
                        a[j][i]=m;
                    }
                    if(i==j)//assigning all the values to -1 for same location distance
                    {
                        arr[i][j]=-1;
                    }
                    else
                    {	continue;}

                }

            }
        }
        if(opt==0)
        {
            for(i=1;i<nof;i++)//for entering distance between all the nodes
            {
                for(j=1;j<nof;j++)
                {
                    if(i!=j)
                    {
                        System.out.print("Enter distance between "+i+"and "+ j+":");
                        String s=buf.readLine();
                        int m= Integer.parseInt(s);
                        arr[i][j]=m;//original array on which we will perform the algorithm
                        //arr[j][i]=m;
                        a[i][j]=m;//duplicating the original array for back up
                        //a[j][i]=m;
                    }
                    if(i==j)//assigning all the values to -1 for same location distance
                    {
                        arr[i][j]=-1;
                    }
                    else
                    {	continue;}

                }

            }
        }
        arr[0][0]=-1;
        arr[0][1]=0;
        arr[0][loc2]=-1;
        for(i=2;i<nof-1;i++)
        {
            if(i!=loc2)
            {
                arr[0][i]=arr[loc1][i];
            }
        }
        arr[loc2][0]=0;
        arr[loc1][0]=-1;
        for(i=2;i<nof-1;i++)
        {
            if(i!=loc1)
            {
                arr[i][0]=arr[i][loc2];
            }
        }


        for(i=0;i<=nof;i++)//printing the array received from user input
        {
            for(j=0;j<=nof;j++)
            {
                System.out.print(arr[i][j]+ " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void prun()
    {
        int i, j;
        fin[0][0] = 0;
        fin[0][1] = 1;
        fin[1][0] = nof - 1;
        fin[1][1] = 0;

        for(i = 0; i < nof; i++)
        {
            for(j = 0; j <= nof; j++)
            {
                arr[i][j] = arr[i+1][j];
            }
        }

        for(i = 0; i <= nof; i++)
        {
            for(j = 1; j < nof; j++)
            {
                arr[i][j] = arr[i][j+1];
            }
        }
        --nof;

        for(i = (nof - 1); i < nof; i++)
        {
            for(j = 0; j <= nof; j++)
            {
                arr[i][j] = arr[i+1][j];
            }
        }

        for(i = 0; i <= nof; i++)
        {
            for(j = 0; j < nof; j++)
            {
                arr[i][j] = arr[i][j+1];
            }
        }
        --nof;
    }

    // For column and row minimization
    public static void minimization()
    {
        for(int i = 0; i < nof; i++)
        {
            long min = min(i, 0);  // To find the row minimal value and 0 here means to check for row minimal
            for(int j = 0; j < nof; j++)
            {
                if(arr[i][j] != -1)
                {
                    arr[i][j] = arr[i][j] - min;
                }
            }
        }

        for(int i = 0; i < nof; i++)
        {
            long min = min(i, 1);      // 1 means column minimal
            for(int j = 0; j < nof; j++)
            {
                if(arr[j][i] != -1)
                {
                    arr[j][i] = arr[j][i] - min;
                }
            }
        }

        System.out.println("After minimization the matrix is :");
        for(int i = 0; i < nof; i++)
        {
            for(int j = 0; j < nof; j++)
            {
                System.out.print(arr[i][j] + "\t ");
            }
            System.out.println(arr[i][nof]);
        }

        for(int i = 0; i < nof; i++)
        {
            System.out.print(arr[nof][i] + "\t ");
        }
        System.out.println();
    }

    // Method for finding the minimal value for row and column
    public static long min(int k, int z)
    {
        long min = 99999999;
        if(z == 0) {
            for(int i = 0; i < nof; i++)
            {
                if( (arr[k][i] < min) && (arr[k][i] != -1) )
                {
                    min = arr[k][i];
                }
            }
        }

        if(z == 1)
        {
            for(int i = 0; i < nof; i++)
            {
                if((arr[i][k] < min) && (arr[i][k] != -1)) {
                    min = arr[i][k];
                }
            }
        }

        return min;
    }

    // Method to find minimum value in row and column during penalty calculation
    public static long minn(int k, int eli, int z)
    {
        long min = 9999999;
        int cost = 0;
        if(z == 0)
        {
            for(int d = 0; d < nof; d++)
            {
                if( (arr[k][d] != -1) && (d != eli) ) {
                    cost = 1;
                }
            }

            if(cost == 0)
            {
                min = 0;
            }
            else
            {
                for(int i = 0; i < nof; i++)
                {
                    if( (arr[k][i] < min) && (arr[k][i] != -1) && (i != eli))
                    {
                        min = arr[k][i];
                    }
                }
            }
        } else if(z == 1) {
            for(int d = 0; d < nof; d++) {
                if( (arr[d][k] != -1) && (d != eli) ) {
                    cost = 1;
                }
            }
            if(cost == 0) {

                min = 0;
            } else {
                for(int i = 0; i < nof; i++) {
                    if( (arr[i][k] < min) && (arr[i][k] != -1) && (i != eli)) {
                        min = arr[i][k];
                    }
                }
            }
        }

        return min;
    }

    //
    public static void penalty()
    {
        int zz = 0, i = 0, j = 0, r = 0, c = 0, ak = 0, flag = 0, trigger = 0;
        long mint=-1;
        int rt[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        int ct[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

        do
        {
            mint = -1;
            for(i = 0; i < nof; i++)
            {
                for(j = 0; j < nof; j++)
                {
                    if(arr[i][j] == 0)
                    {
                        flag = 0;
                        for(int p = 0; p < ak; p++) {
                            if( (rt[p] == i) && (ct[p] == j) ) {

                                flag = 1;
                            }
                        }

                        if(flag == 0) {

                            long minr = minn(i, j, 0);
                            long minc = minn(j, i, 1);
                            long temp = minr + minc;
                            if(mint < temp) {

                                mint = temp;
                                r = i;
                                c = j;
                            }
                        }
                    }
                }
            }

            int rcheck = 0;
            int ccheck = 0;
            int rowrec = -1;
            int colrec = -1;
            int rw = (int) arr[r][nof];
            int cl = (int) arr[nof][c];
            if(checkloop(rw, cl)) {

                trigger = 0;
                rt[ak] = r;
                ct[ak] = c;
                ++ak;
            } else {

                trigger = 1;
                fin[f1][0] = rw;
                fin[f1][1] = cl;
                f1++;

                sum = sum + a[rw][cl];
                System.out.println("Sum is" + sum);
                System.out.println("Nof: " + nof);
                for(int ch = 0; ch < nof; ch++) {
                    if(arr[nof][ch] == rw) {
                        rcheck = 1;
                        rowrec = ch;
                    }
                    if(arr[ch][nof] == cl) {
                        ccheck = 1;
                        colrec = ch;
                    }
                }

                System.out.println("rcheck: " + rcheck + "\t" + "ccheck: " + ccheck);
                if((rcheck == 1) && (ccheck == 1))
                {
                    arr[colrec][rowrec] = -1;
                }

                System.out.println("r: " + r + "  c: " + c);
                System.out.println("mint is: " + mint);
                for(i = r; i < nof; i++) {
                    for(j = 0; j <= nof; j++) {
                        arr[i][j] = arr[i+1][j];
                    }
                }

                for( i = 0; i <= nof; i++) {
                    for(j = c; j < nof; j++) {
                        arr[i][j] = arr[i][j+1];
                    }
                }

                nof--;
            }
        } while(trigger == 0);
    }

    public static boolean checkloop(int r, int c)
    {
        int i = c,t = 0;
        traceroute[r] = c;
        do
        {
            i = traceroute[i];
            t++;
            if(i == -1) {
                System.out.print("no loop");
                return false;
            }
        } while(i != c);

        if(t != z) {

            traceroute[r] = -1;
            System.out.println("loop formed " + r + c);
            return true;
        } else {

            System.out.print("t must be equal to z: " + t);
            return false;
        }
    }

    public static void path() {

        int i;
        for(i = 0; i < f1; i++) {
            if(fin[i][0] == 1) {
                break;
            }
        }

        while(z > 2) {

            System.out.println( fin[i][0] + "->" + fin[i][1]);
            --z;
            for(int j = 0; j < f1; j++) {
                if(fin[i][1] == fin[j][0])
                {
                    i = j;
                    break;
                }
            }
        }

        System.out.println("Total distance covered is: " + sum);
    }

    public static void main(String args[]) throws IOException
    {
        StraightTrip t = new StraightTrip();
        t.dist();
        t.prun();
        while(nof > 0) {

            t.minimization();
            t.penalty();
        }

        t.path();
    }
}
