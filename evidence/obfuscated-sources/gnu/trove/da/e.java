package gnu.trove.da;

import gnu.trove.c_ref.aa;
import gnu.trove.c_ref.al;
import gnu.trove.b;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class e implements aa, Externalizable {
   static final long serialVersionUID = 1L;
   private long[] a;
   private int b;
   private long c;

   public e() {
      this(10, 0L);
   }

   private e(int var1, long var2) {
      this.a = new long[10];
      this.b = 0;
      this.c = 0L;
   }

   public final int a() {
      return this.b;
   }

   public final boolean a(long var1) {
      int var4 = this.b + 1;
      if (var4 > this.a.length) {
         long[] var5 = new long[Math.max(this.a.length << 1, var4)];
         System.arraycopy(this.a, 0, var5, 0, this.a.length);
         this.a = var5;
      }

      this.a[this.b++] = var1;
      return true;
   }

   public final long a(int var1) {
      if (var1 >= this.b) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this.a[var1];
      }
   }

   public final void b() {
      this.b = 0;
      Arrays.fill(this.a, this.c);
   }

   public final void a(int var1, int var2) {
      if (var1 >= 0 && var1 < this.b) {
         if (var1 == 0) {
            System.arraycopy(this.a, 1, this.a, 0, this.b - 1);
         } else if (this.b - 1 != var1) {
            System.arraycopy(this.a, var1 + 1, this.a, var1, this.b - (var1 + 1));
         }

         --this.b;
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public final aa iterator() {
      return new f(this, 0);
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof e)) {
         return false;
      } else {
         e var3;
         if ((var3 = (e)var1).b != this.b) {
            return false;
         } else {
            int var2 = this.b;

            while(var2-- > 0) {
               if (this.a[var2] != var3.a[var2]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public final int hashCode() {
      int var1 = 0;

      for(int var2 = this.b; var2-- > 0; var1 += gnu.trove.b_ref.b.a(this.a[var2])) {
      }

      return var1;
   }

   public final boolean contains(long var1) {
      long var8 = var1;
      int var2 = this.b;
      e var10 = this;
      var2 = var2;

      int var10000;
      while(true) {
         if (var2-- <= 0) {
            var10000 = -1;
            break;
         }

         if (var10.a[var2] == var8) {
            var10000 = var2;
            break;
         }
      }

      return var10000 >= 0;
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder("{");
      int var2 = 0;

      for(int var3 = this.b - 1; var2 < var3; ++var2) {
         var1.append(this.a[var2]);
         var1.append(", ");
      }

      if (this.b > 0) {
         var1.append(this.a[this.b - 1]);
      }

      var1.append("}");
      return var1.toString();
   }

   @Override
   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           var1.writeInt(this.b);
           var1.writeLong(this.c);
           int var2 = this.a.length;
           var1.writeInt(var2);
           
           for(int var3 = 0; var3 < var2; ++var3) {
               var1.writeLong(this.a[var3]);
           }} catch (IOException ex) {
           Logger.getLogger(e.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   @Override
   public final void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           this.b = var1.readInt();
           this.c = var1.readLong();
           int var2 = var1.readInt();
           this.a = new long[var2];
           
           for(int var3 = 0; var3 < var2; ++var3) {
               this.a[var3] = var1.readLong();
           }} catch (IOException ex) {
           Logger.getLogger(e.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

    /*@Override
    public boolean contains(char var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(int var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    @Override
    public long next() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
