package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.g;
import gnu.trove.a_ref.a;
import gnu.trove.b_ref.b;
import gnu.trove.ba.R_ref;
import gnu.trove.c_ref.T_ref;
import gnu.trove.e_ref.S_ref;
import gnu.trove.e_ref.ar;
import gnu.trove.f_ref.e;
import gnu.trove.map.N_ref;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TIntShortHashMap extends R_ref implements N_ref, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TIntShortHashMap() {
   }

   public TIntShortHashMap(int var1) {
      super(var1);
   }

   public TIntShortHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TIntShortHashMap(int var1, float var2, int var3, short var4) {
      super(var1, var2, var3, var4);
   }

   public TIntShortHashMap(int[] var1, short[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TIntShortHashMap(N_ref var1) {
      super(var1.size());
      if (var1 instanceof TIntShortHashMap) {
         TIntShortHashMap var2 = (TIntShortHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new short[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      int[] var3 = this._set;
      short[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new int[var1];
      this._values = new short[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var5[var1] == 1) {
            var2 = var3[var1];
            var2 = this.insertKey(var2);
            this._values[var2] = var4[var1];
         }
      }

   }

   public short put(int var1, short var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   public short putIfAbsent(int var1, short var2) {
      int var3;
      return (var3 = this.insertKey(var1)) < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private short doPut(int var1, short var2, int var3) {
      short var5 = this.no_entry_value;
      boolean var4 = true;
      if (var3 < 0) {
         var3 = -var3 - 1;
         var5 = this._values[var3];
         var4 = false;
      }

      this._values[var3] = var2;
      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Integer)var3.getKey(), (Short)var3.getValue());
      }

   }

   public void putAll(N_ref var1) {
      this.ensureCapacity(var1.size());
      T_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public short get(int var1) {
      return (var1 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var1];
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
   }

   public boolean isEmpty() {
      return this._size == 0;
   }

   public short remove(int var1) {
      short var2 = this.no_entry_value;
      if ((var1 = this.index(var1)) >= 0) {
         var2 = this._values[var1];
         this.removeAt(var1);
      }

      return var2;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public e keySet() {
      return new TIntShortHashMap$TKeyView(this);
   }

   public int[] keys() {
      int[] var1 = new int[this.size()];
      int[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public int[] keys(int[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new int[var2];
      }

      int[] var6 = this._set;
      byte[] var3 = this._states;
      int var4 = var6.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var6[var4];
         }
      }

      return var1;
   }

   public g valueCollection() {
      return new TIntShortHashMap$TValueView(this);
   }

   public short[] values() {
      short[] var1 = new short[this.size()];
      short[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public short[] values(short[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new short[var2];
      }

      short[] var6 = this._values;
      byte[] var3 = this._states;
      int var4 = var6.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var6[var4];
         }
      }

      return var1;
   }

   public boolean containsValue(short var1) {
      byte[] var2 = this._states;
      short[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var2[var4] != 1 || var1 != var3[var4]);

      return true;
   }

   public boolean containsKey(int var1) {
      return this.contains(var1);
   }

   public T_ref iterator() {
      return new TIntShortHashMap$TIntShortHashIterator(this, this);
   }

   public boolean forEachKey(gnu.trove.e_ref.R_ref var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(ar var1) {
      byte[] var2 = this._states;
      short[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(S_ref var1) {
      byte[] var2 = this._states;
      int[] var3 = this._set;
      short[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues$3f6b603b(a var1) {
      byte[] var2 = this._states;
      short[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.h();
         }
      }

   }

   public boolean retainEntries(S_ref var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      int[] var4 = this._set;
      short[] var5 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var6 = var4.length;

         while(var6-- > 0) {
            if (var3[var6] == 1 && !var1.execute(var4[var6], var5[var6])) {
               this.removeAt(var6);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   public boolean increment(int var1) {
      return this.adjustValue(var1, (short)1);
   }

   public boolean adjustValue(int var1, short var2) {
      if ((var1 = this.index(var1)) < 0) {
         return false;
      } else {
         short[] var10000 = this._values;
         var10000[var1] += var2;
         return true;
      }
   }

   public short adjustOrPutValue(int var1, short var2, short var3) {
      boolean var4;
      if ((var1 = this.insertKey(var1)) < 0) {
         var1 = -var1 - 1;
         short[] var10000 = this._values;
         var2 = var10000[var1] += var2;
         var4 = false;
      } else {
         var2 = this._values[var1] = var3;
         var4 = true;
      }

      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof N_ref)) {
         return false;
      } else {
         N_ref var9;
         if ((var9 = (N_ref)var1).size() != this.size()) {
            return false;
         } else {
            short[] var2 = this._values;
            byte[] var3 = this._states;
            short var4 = this.getNoEntryValue();
            short var5 = var9.getNoEntryValue();
            int var6 = var2.length;

            while(var6-- > 0) {
               if (var3[var6] == 1) {
                  int var7 = this._set[var6];
                  short var10 = var9.get(var7);
                  short var8;
                  if ((var8 = var2[var6]) != var10 && var8 != var4 && var10 != var5) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      byte[] var2 = this._states;
      int var3 = this._values.length;

      while(var3-- > 0) {
         if (var2[var3] == 1) {
            var1 += b.a(this._set[var3]) ^ b.a(this._values[var3]);
         }
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TIntShortHashMap$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   public void writeExternal(ObjectOutput var1) {
   try {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while(var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeInt(this._set[var2]);
            var1.writeShort(this._values[var2]);
         }
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   public void readExternal(ObjectInput var1) {
   try {
      var1.readByte();
      super.readExternal(var1);
      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         int var3 = var1.readInt();
         short var4 = var1.readShort();
         this.put(var3, var4);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   // $FF: synthetic method
   static int access$0(TIntShortHashMap var0) {
      return var0.no_entry_key;
   }

   // $FF: synthetic method
   static int access$1(TIntShortHashMap var0) {
      return var0._size;
   }

   // $FF: synthetic method
   static short access$2(TIntShortHashMap var0) {
      return var0.no_entry_value;
   }
}
