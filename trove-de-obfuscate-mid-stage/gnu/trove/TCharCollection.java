package gnu.trove;

import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TIterator;

/*public abstract interface b {
   public abstract boolean contains(char var1);

   public abstract p iterator();
}*/

public abstract interface TCharCollection {
   public abstract boolean contains(char var1);

   public abstract TIterator iterator();
}
