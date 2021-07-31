package kr.ac.kumoh.s20140716.cctvmap;

import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

public class doPermutation {

    int j = 0;
    private ArrayList<ArrayList<TMapPoint>> list = new ArrayList<>();

    public ArrayList<ArrayList<TMapPoint>> doPermutation(ArrayList<TMapPoint> passlist, int startIdx) {
        int length = passlist.size();
        ArrayList<TMapPoint> passlist_Permutaion = new ArrayList<TMapPoint>();

        if (startIdx == length - 1)
        {

            for(int i = 0; i < length; i++)
            {
                passlist_Permutaion.add(i, passlist.get(i));
            }
            list.add(j, passlist_Permutaion);
            j++;
        }

        for(int i = startIdx; i < length; i++)
        {
            swap(passlist, startIdx, i);
            doPermutation(passlist, startIdx + 1);
            swap(passlist, startIdx, i);
        }
        return list;
    }

    public void swap(ArrayList<TMapPoint> passlist, int n1, int n2)
    {
        TMapPoint temp = passlist.get(n1);
        passlist.set(n1, passlist.get(n2));
        passlist.set(n2, temp);
    }

}
