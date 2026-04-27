package gnu.trove.ba;

import java.util.Arrays;

public abstract class e extends ab {
   static final long serialVersionUID = 1L;
   public transient byte[] _set;
   protected byte no_entry_value;
   protected boolean consumeFreeSlot;

   public e() {
      this.no_entry_value = gnu.trove.b_ref.a.a;
      if (this.no_entry_value != 0) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public e(int var1) {
      super(var1);
      this.no_entry_value = gnu.trove.b_ref.a.a;
      if (this.no_entry_value != 0) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public e(int var1, float var2) {
      super(var1, var2);
      this.no_entry_value = gnu.trove.b_ref.a.a;
      if (this.no_entry_value != 0) {
         Arrays.fill(this._set, this.no_entry_value);
      }

   }

   public e(int var1, float var2, byte var3) {
      super(var1, var2);
      this.no_entry_value = var3;
      if (var3 != 0) {
         Arrays.fill(this._set, var3);
      }

   }

   public byte getNoEntryValue() {
      return this.no_entry_value;
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._set = new byte[var1];
      return var1;
   }

   public boolean contains(byte var1) {
      return this.index(var1) >= 0;
   }

   public boolean forEach(gnu.trove.e_ref.h var1) {
      byte[] var2 = this._states;
      byte[] var3;
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

   protected int index(byte var1) {
      byte[] var4 = this._states;
      byte[] var5 = this._set;
      int var3 = var4.length;
      int var2;
      var3 = (var2 = gnu.trove.b_ref.b.a(var1) & Integer.MAX_VALUE) % var3;
      byte var6;
      if ((var6 = var4[var3]) == 0) {
         return -1;
      } else {
         return var6 == 1 && var5[var3] == var1 ? var3 : this.indexRehashed(var1, var3, var2, var6);
      }
   }

   int indexRehashed(byte var1, int var2, int var3, byte var4) {
      int var5 = this._set.length;
      var3 = 1 + var3 % (var5 - 2);
      int var6 = var2;

      do {
         if ((var2 -= var3) < 0) {
            var2 += var5;
         }

         if ((var4 = this._states[var2]) == 0) {
            return -1;
         }

         if (var1 == this._set[var2] && var4 != 2) {
            return var2;
         }
      } while(var2 != var6);

      return -1;
   }

   protected int insertKey(byte var1) {
      int var2;
      int var3 = (var2 = gnu.trove.b_ref.b.a(var1) & Integer.MAX_VALUE) % this._states.length;
      byte var4 = this._states[var3];
      this.consumeFreeSlot = false;
      if (var4 == 0) {
         this.consumeFreeSlot = true;
         this.insertKeyAt(var3, var1);
         return var3;
      } else {
         return var4 == 1 && this._set[var3] == var1 ? -var3 - 1 : this.insertKeyRehash(var1, var3, var2, var4);
      }
   }

   int insertKeyRehash(byte var1, int var2, int var3, byte var4) {
      int var5 = this._set.length;
      var3 = 1 + var3 % (var5 - 2);
      int var6 = var2;
      int var7 = -1;

      do {
         if (var4 == 2 && var7 == -1) {
            var7 = var2;
         }

         if ((var2 -= var3) < 0) {
            var2 += var5;
         }

         if ((var4 = this._states[var2]) == 0) {
            if (var7 != -1) {
               this.insertKeyAt(var7, var1);
               return var7;
            }

            this.consumeFreeSlot = true;
            this.insertKeyAt(var2, var1);
            return var2;
         }

         if (var4 == 1 && this._set[var2] == var1) {
            return -var2 - 1;
         }
      } while(var2 != var6);

      if (var7 != -1) {
         this.insertKeyAt(var7, var1);
         return var7;
      } else {
         throw new IllegalStateException("No free or removed slots available. Key set full?!!");
      }
   }

   void insertKeyAt(int var1, byte var2) {
      this._set[var1] = var2;
      this._states[var1] = 1;
   }
}
