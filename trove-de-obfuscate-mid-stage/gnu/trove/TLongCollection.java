package gnu.trove;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TIterator;

/*public abstract interface f {
   public abstract boolean contains(long var1);

   public abstract aa iterator();
}*/

public abstract interface TLongCollection {
   public abstract boolean contains(long var1);

   public abstract TIterator iterator();
}
