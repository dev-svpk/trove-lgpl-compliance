package gnu.trove;

import gnu.trove.iterator.TDoubleIterator;

public abstract interface TDoubleCollection {
   public abstract boolean contains(double var1);

   public abstract TDoubleIterator iterator();
}
