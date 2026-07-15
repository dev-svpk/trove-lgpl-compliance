package gnu.trove.impl.hash;

import java.util.Arrays;

public abstract class TDoubleHash extends TPrimitiveHash {
   static final long serialVersionUID = 1L;
   public transient double[] _set;
   protected double no_entry_value;
   protected boolean consumeFreeSlot;

   public TDoubleHash() {
      this.no_entry_value = gnu.trove.impl.Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
      if (this.no_entry_value != 0.0D) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public TDoubleHash(int var1) {
      super(var1);
      this.no_entry_value = gnu.trove.impl.Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
      if (this.no_entry_value != 0.0D) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public TDoubleHash(int var1, float var2) {
      super(var1, var2);
      this.no_entry_value = gnu.trove.impl.Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
      if (this.no_entry_value != 0.0D) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public TDoubleHash(int var1, float var2, double var3) {
      super(var1, var2);
      this.no_entry_value = var3;
      if (var3 != 0.0D) {
         Arrays.fill(this._set, var3);
      }

   }

   public double getNoEntryValue() {
      return this.no_entry_value;
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._set = new double[var1];
      return var1;
   }

   public boolean contains(double var1) {
      return this.index(var1) >= 0;
   }

   public boolean forEach(gnu.trove.procedure.TDoubleProcedure var1) {
      byte[] var2 = this._states;
      double[] var3;
      int var4 = (var3 = this._set).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   protected void removeAt(int var1) {
      this._set[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   protected int index(double var1) {
      byte[] var5 = this._states;
      double[] var6 = this._set;
      int var4 = var5.length;
      int var3;
      var4 = (var3 = gnu.trove.impl.HashFunctions.hash(var1) & Integer.MAX_VALUE) % var4;
      byte var7;
      if ((var7 = var5[var4]) == 0) {
         return -1;
      } else {
         return var7 == 1 && var6[var4] == var1 ? var4 : this.indexRehashed(var1, var4, var3, var7);
      }
   }

   int indexRehashed(double var1, int var3, int var4, byte var5) {
      int var6 = this._set.length;
      var4 = 1 + var4 % (var6 - 2);
      int var7 = var3;

      do {
         if ((var3 -= var4) < 0) {
            var3 += var6;
         }

         if ((var5 = this._states[var3]) == 0) {
            return -1;
         }

         if (var1 == this._set[var3] && var5 != 2) {
            return var3;
         }
      } while(var3 != var7);

      return -1;
   }

   protected int insertKey(double var1) {
      int var3;
      int var4 = (var3 = gnu.trove.impl.HashFunctions.hash(var1) & Integer.MAX_VALUE) % this._states.length;
      byte var5 = this._states[var4];
      this.consumeFreeSlot = false;
      if (var5 == 0) {
         this.consumeFreeSlot = true;
         this.insertKeyAt(var4, var1);
         return var4;
      } else {
         return var5 == 1 && this._set[var4] == var1 ? -var4 - 1 : this.insertKeyRehash(var1, var4, var3, var5);
      }
   }

   int insertKeyRehash(double var1, int var3, int var4, byte var5) {
      int var6 = this._set.length;
      var4 = 1 + var4 % (var6 - 2);
      int var7 = var3;
      int var8 = -1;

      do {
         if (var5 == 2 && var8 == -1) {
            var8 = var3;
         }

         if ((var3 -= var4) < 0) {
            var3 += var6;
         }

         if ((var5 = this._states[var3]) == 0) {
            if (var8 != -1) {
               this.insertKeyAt(var8, var1);
               return var8;
            }

            this.consumeFreeSlot = true;
            this.insertKeyAt(var3, var1);
            return var3;
         }

         if (var5 == 1 && this._set[var3] == var1) {
            return -var3 - 1;
         }
      } while(var3 != var7);

      if (var8 != -1) {
         this.insertKeyAt(var8, var1);
         return var8;
      } else {
         throw new IllegalStateException("No free or removed slots available. Key set full?!!");
      }
   }

   void insertKeyAt(int var1, double var2) {
      this._set[var1] = var2;
      this._states[var1] = 1;
   }
}
