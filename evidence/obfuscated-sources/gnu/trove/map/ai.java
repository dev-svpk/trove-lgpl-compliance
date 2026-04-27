package gnu.trove.map;

import gnu.trove.c_ref.aq;

public interface ai {
   int getNoEntryValue();

   int get(short var1);

   int size();

   aq iterator();
}
