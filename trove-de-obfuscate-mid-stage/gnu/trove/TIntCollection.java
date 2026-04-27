package gnu.trove;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIterator;

/*public abstract interface e {
   public abstract boolean contains(int var1);

   public abstract Q_ref iterator();
}*/

public abstract interface TIntCollection {
   public abstract boolean contains(int var1);

   public abstract TIterator iterator();
}
