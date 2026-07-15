package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.TLongCollection;
import gnu.trove.a_ref.a;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TShortLongHash;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import gnu.trove.map.TShortLongMap;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TShortLongHashMap extends TShortLongHash implements TShortLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TShortLongHashMap() {
   }

   public TShortLongHashMap(int var1) {
      super(var1);
   }

   public TShortLongHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TShortLongHashMap(int var1, float var2, short var3, long var4) {
      super(var1, var2, var3, var4);
   }

   public TShortLongHashMap(short[] var1, long[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TShortLongHashMap(TShortLongMap var1) {
      super(var1.size());
      if (var1 instanceof TShortLongHashMap) {
         TShortLongHashMap var2 = (TShortLongHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new long[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      short[] var3 = this._set;
      long[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new short[var1];
      this._values = new long[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var5[var1] == 1) {
            short var6 = var3[var1];
            var2 = this.insertKey(var6);
            this._values[var2] = var4[var1];
         }
      }

   }

   public long put(short var1, long var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var2, var4);
   }

   public long putIfAbsent(short var1, long var2) {
      int var4;
      return (var4 = this.insertKey(var1)) < 0 ? this._values[-var4 - 1] : this.doPut(var1, var2, var4);
   }

   private long doPut(short var1, long var2, int var4) {
      long var5 = this.no_entry_value;
      boolean var7 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var7 = false;
      }

      this._values[var4] = var2;
      if (var7) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Short)var3.getKey(), (Long)var3.getValue());
      }

   }

   public void putAll(TShortLongMap var1) {
      this.ensureCapacity(var1.size());
      TShortLongIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public long get(short var1) {
      int var2;
      return (var2 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var2];
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

   public long remove(short var1) {
      long var2 = this.no_entry_value;
      int var4;
      if ((var4 = this.index(var1)) >= 0) {
         var2 = this._values[var4];
         this.removeAt(var4);
      }

      return var2;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public TShortSet keySet() {
      return new TShortLongHashMap$TKeyView(this);
   }

   public short[] keys() {
      short[] var1 = new short[this.size()];
      short[] var2 = this._set;
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

   public short[] keys(short[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new short[var2];
      }

      short[] var6 = this._set;
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

   public TLongCollection valueCollection() {
      return new TShortLongHashMap$TValueView(this);
   }

   public long[] values() {
      long[] var1 = new long[this.size()];
      long[] var2 = this._values;
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

   public long[] values(long[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new long[var2];
      }

      long[] var6 = this._values;
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

   public boolean containsValue(long var1) {
      byte[] var3 = this._states;
      long[] var4;
      int var5 = (var4 = this._values).length;

      do {
         if (var5-- <= 0) {
            return false;
         }
      } while(var3[var5] != 1 || var1 != var4[var5]);

      return true;
   }

   public boolean containsKey(short var1) {
      return this.contains(var1);
   }

   public TShortLongIterator iterator() {
      return new TShortLongHashMap$TShortLongHashIterator(this, this);
   }

   public boolean forEachKey(TShortProcedure var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(TLongProcedure var1) {
      byte[] var2 = this._states;
      long[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(TShortLongProcedure var1) {
      byte[] var2 = this._states;
      short[] var3 = this._set;
      long[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues(a var1) {
      byte[] var2 = this._states;
      long[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.f();
         }
      }

   }

   public boolean retainEntries(TShortLongProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      short[] var4 = this._set;
      long[] var5 = this._values;
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

   public boolean increment(short var1) {
      return this.adjustValue(var1, 1L);
   }

   public boolean adjustValue(short var1, long var2) {
      int var4;
      if ((var4 = this.index(var1)) < 0) {
         return false;
      } else {
         long[] var10000 = this._values;
         var10000[var4] += var2;
         return true;
      }
   }

   public long adjustOrPutValue(short var1, long var2, long var4) {
      long var8;
      int var10;
      boolean var11;
      if ((var10 = this.insertKey(var1)) < 0) {
         var10 = -var10 - 1;
         long[] var10000 = this._values;
         var8 = var10000[var10] += var2;
         var11 = false;
      } else {
         var8 = this._values[var10] = var4;
         var11 = true;
      }

      if (var11) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var8;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TShortLongMap)) {
         return false;
      } else {
         TShortLongMap var15;
         if ((var15 = (TShortLongMap)var1).size() != this.size()) {
            return false;
         } else {
            long[] var2 = this._values;
            byte[] var3 = this._states;
            long var5 = this.getNoEntryValue();
            long var7 = var15.getNoEntryValue();
            int var4 = var2.length;

            while(var4-- > 0) {
               if (var3[var4] == 1) {
                  short var9 = this._set[var4];
                  long var11 = var15.get(var9);
                  long var13;
                  if ((var13 = var2[var4]) != var11 && var13 != var5 && var11 != var7) {
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
            var1 += HashFunctions.hash(this._set[var3]) ^ HashFunctions.hash(this._values[var3]);
         }
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TShortLongHashMap$1(this, var1));
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
            var1.writeShort(this._set[var2]);
            var1.writeLong(this._values[var2]);
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
         short var3 = var1.readShort();
         long var4 = var1.readLong();
         this.put(var3, var4);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   // $FF: synthetic method
   static short access$0(TShortLongHashMap var0) {
      return var0.no_entry_key;
   }

   // $FF: synthetic method
   static int access$1(TShortLongHashMap var0) {
      return var0._size;
   }

   // $FF: synthetic method
   static long access$2(TShortLongHashMap var0) {
      return var0.no_entry_value;
   }
}
