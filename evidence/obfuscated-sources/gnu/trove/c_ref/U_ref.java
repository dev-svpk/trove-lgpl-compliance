package gnu.trove.c_ref;

public abstract interface U_ref {
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
