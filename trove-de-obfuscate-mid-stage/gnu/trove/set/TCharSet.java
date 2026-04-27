package gnu.trove.set;

public abstract interface TCharSet extends gnu.trove.TCharCollection {
   public abstract int size();

   @Override
   public abstract boolean contains(char var1);
}
