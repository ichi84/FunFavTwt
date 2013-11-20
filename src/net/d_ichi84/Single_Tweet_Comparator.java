package net.d_ichi84;

import java.util.Comparator;
import data.Single_Tweet;

public class Single_Tweet_Comparator implements Comparator<Single_Tweet> {

	@Override
	public int compare(Single_Tweet arg0, Single_Tweet arg1) {
		return -(arg0.id.compareTo(arg1.id));
		//Integer.valueOf(arg0.id) > Integer.valueOf(arg1.id); 
	}

}
