package gnu.trove.da;

import gnu.trove.c_ref.Q_ref;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

final class d implements Q_ref {
   private int a;
   private int b;
   // $FF: synthetic field
   private c c;

   d(c var1, int var2) {
      super();
      this.c = var1;      
      this.a = 0;
      this.b = -1;
      this.a = 0;
   }

   @Override
   public final boolean hasNext() {
      return this.a < this.c.a();
   }

   @Override
   public final int next() {
      try {
         int var1 = this.c.b(this.a);
         this.b = this.a++;
         return var1;
      } catch (IndexOutOfBoundsException var2) {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final void remove() {
      if (this.b == -1) {
         throw new IllegalStateException();
      } else {
         try {
            this.c.a(this.b, 1);
            if (this.b < this.a) {
               --this.a;
            }

            this.b = -1;
         } catch (IndexOutOfBoundsException var1) {
            throw new ConcurrentModificationException();
         }
      }
   }

    @Override
    public byte nextByte() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextInt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public char nextChar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long nextLong() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float nextFloat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short nextShort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double nextDouble() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
