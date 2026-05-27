package gnu.trove.iterator;

public abstract interface TIterator {
   public abstract boolean hasNext();

   public abstract void remove();
   
   public byte nextByte(); //g
   public int nextInt(); //Q_ref
   public char nextChar(); //p
   public long nextLong(); //aa
   public float nextFloat(); //H_ref
   public short nextShort(); //ar
   public double nextDouble(); //y
}
