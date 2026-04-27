package gnu.trove.impl.hash;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TFloatShortHash extends TPrimitiveHash {
   static final long serialVersionUID = 1L;
   public transient float[] _set;
   protected float no_entry_key;
   protected short no_entry_value;
   protected boolean consumeFreeSlot;

   public TFloatShortHash() {
      this.no_entry_key = 0.0F;
      this.no_entry_value = 0;
   }

   public TFloatShortHash(int var1) {
      super(var1);
      this.no_entry_key = 0.0F;
      this.no_entry_value = 0;
   }

   public TFloatShortHash(int var1, float var2) {
      super(var1, var2);
      this.no_entry_key = 0.0F;
      this.no_entry_value = 0;
   }

   public TFloatShortHash(int var1, float var2, float var3, short var4) {
      super(var1, var2);
      this.no_entry_key = var3;
      this.no_entry_value = var4;
   }

   public float getNoEntryKey() {
      return this.no_entry_key;
   }

   public short getNoEntryValue() {
      return this.no_entry_value;
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._set = new float[var1];
      return var1;
   }

   public boolean contains(float var1) {
      return this.index(var1) >= 0;
   }

   public boolean forEach(gnu.trove.procedure.TFloatProcedure var1) {
      byte[] var2 = this._states;
      float[] var3;
      int var4 = (var3 = this._set).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   protected void removeAt(int var1) {
      this._set[var1] = this.no_entry_key;
      super.removeAt(var1);
   }

   protected int index(float var1) {
      byte[] var4 = this._states;
      float[] var5 = this._set;
      int var3 = var4.length;
      int var2;
      var3 = (var2 = gnu.trove.impl.HashFunctions.hash(var1) & Integer.MAX_VALUE) % var3;
      byte var6;
      if ((var6 = var4[var3]) == 0) {
         return -1;
      } else {
         return var6 == 1 && var5[var3] == var1 ? var3 : this.indexRehashed(var1, var3, var2, var6);
      }
   }

   int indexRehashed(float var1, int var2, int var3, byte var4) {
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

   protected int insertKey(float var1) {
      int var2;
      int var3 = (var2 = gnu.trove.impl.HashFunctions.hash(var1) & Integer.MAX_VALUE) % this._states.length;
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

   int insertKeyRehash(float var1, int var2, int var3, byte var4) {
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

   void insertKeyAt(int var1, float var2) {
      this._set[var1] = var2;
      this._states[var1] = 1;
   }

   protected int XinsertKey(float var1) {
      byte[] var5 = this._states;
      float[] var6 = this._set;
      int var4 = var5.length;
      int var2;
      int var3 = (var2 = gnu.trove.impl.HashFunctions.hash(var1) & Integer.MAX_VALUE) % var4;
      byte var7 = var5[var3];
      this.consumeFreeSlot = false;
      if (var7 == 0) {
         this.consumeFreeSlot = true;
         var6[var3] = var1;
         var5[var3] = 1;
         return var3;
      } else if (var7 == 1 && var6[var3] == var1) {
         return -var3 - 1;
      } else {
         var2 = 1 + var2 % (var4 - 2);
         if (var7 != 2) {
            do {
               if ((var3 -= var2) < 0) {
                  var3 += var4;
               }
            } while((var7 = var5[var3]) == 1 && var6[var3] != var1);
         }

         if (var7 != 2) {
            if (var7 == 1) {
               return -var3 - 1;
            } else {
               this.consumeFreeSlot = true;
               var6[var3] = var1;
               var5[var3] = 1;
               return var3;
            }
         } else {
            int var8;
            for(var8 = var3; var7 != 0 && (var7 == 2 || var6[var3] != var1); var7 = var5[var3]) {
               if ((var3 -= var2) < 0) {
                  var3 += var4;
               }
            }

            if (var7 == 1) {
               return -var3 - 1;
            } else {
               var6[var3] = var1;
               var5[var3] = 1;
               return var8;
            }
         }
      }
   }

   public void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           super.writeExternal(var1);
           var1.writeFloat(this.no_entry_key);
           var1.writeShort(this.no_entry_value);
       } catch (IOException ex) {
           Logger.getLogger(TFloatShortHash.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

   public void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           super.readExternal(var1);
           this.no_entry_key = var1.readFloat();
           this.no_entry_value = var1.readShort();
       } catch (IOException ex) {
           Logger.getLogger(TFloatShortHash.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
