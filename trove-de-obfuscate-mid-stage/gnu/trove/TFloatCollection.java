package gnu.trove;

import gnu.trove.iterator.TFloatIterator;

public abstract interface TFloatCollection {
   public abstract boolean contains(float var1);

   public abstract TFloatIterator iterator();
}
