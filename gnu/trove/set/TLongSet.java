package gnu.trove.set;

public abstract interface TLongSet extends gnu.trove.TLongCollection {
   public abstract int size();

   @Override
   public abstract boolean contains(long var1);
}
