package gnu.trove.da;

import gnu.trove.c_ref.Q_ref;
import gnu.trove.c_ref.al;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class c implements Q_ref, Externalizable {
   static final long serialVersionUID = 1L;
   private int[] a;
   private int b;
   private int c;

   public c() {
      this(10, 0);
   }

   private c(int var1, int var2) {
      this.a = new int[10];
      this.b = 0;
      this.c = 0;
   }

   public final int a() {
      return this.b;
   }

   public final boolean a(int var1) {
      int var3 = this.b + 1;
      if (var3 > this.a.length) {
         int[] var4 = new int[Math.max(this.a.length << 1, var3)];
         System.arraycopy(this.a, 0, var4, 0, this.a.length);
         this.a = var4;
      }

      this.a[this.b++] = var1;
      return true;
   }

   public final int b(int var1) {
      if (var1 >= this.b) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this.a[var1];
      }
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

   public final Q_ref iterator() {
      return new d(this, 0);
   }

   public final void b() {
      int var2 = this.b;
      boolean var1 = false;
      c var8 = this;
      if (var2 != 0) {
         if (var2 < 0) {
            throw new IllegalArgumentException("from cannot be greater than to");
         } else {
            int var3 = 0;
            --var2;

            while(var3 < var2) {
               int var7 = var8.a[var3];
               int[] var10002 = var8.a;
               var10002[var3] = var10002[var2];
               var8.a[var2] = var7;
               ++var3;
               --var2;
            }

         }
      }
   }

   public final int[] c() {
      int var2 = this.b;
      boolean var1 = false;
      int[] var3 = new int[var2];
      byte var4 = 0;
      if (var2 != 0) {
         if (var4 < 0 || var4 >= this.b) {
            throw new ArrayIndexOutOfBoundsException(var4);
         }

         System.arraycopy(this.a, var4, var3, 0, var2);
      }

      return var3;
   }

   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof c)) {
         return false;
      } else {
         c var3;
         if ((var3 = (c)var1).b != this.b) {
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

   public final int hashCode() {
      int var1 = 0;

      for(int var2 = this.b; var2-- > 0; var1 += gnu.trove.b_ref.b.a(this.a[var2])) {
      }

      return var1;
   }

   public final void d() {
      Arrays.sort(this.a, 0, this.b);
   }

   public final boolean contains(int var1) {
      int var3 = var1;
      int var2 = this.b;
      c var4 = this;
      var2 = var2;

      int var10000;
      while(true) {
         if (var2-- <= 0) {
            var10000 = -1;
            break;
         }

         if (var4.a[var2] == var3) {
            var10000 = var2;
            break;
         }
      }

      return var10000 >= 0;
   }

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

   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           var1.writeInt(this.b);
           var1.writeInt(this.c);
           int var2 = this.a.length;
           var1.writeInt(var2);
           
           for(int var3 = 0; var3 < var2; ++var3) {
               var1.writeInt(this.a[var3]);
           }} catch (IOException ex) {
           Logger.getLogger(c.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   public final void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           this.b = var1.readInt();
           this.c = var1.readInt();
           int var2 = var1.readInt();
           this.a = new int[var2];
           
           for(int var3 = 0; var3 < var2; ++var3) {
               this.a[var3] = var1.readInt();
           }} catch (IOException ex) {
           Logger.getLogger(c.class.getName()).log(Level.SEVERE, null, ex);
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
    }*/

    @Override
    public int next() {
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
