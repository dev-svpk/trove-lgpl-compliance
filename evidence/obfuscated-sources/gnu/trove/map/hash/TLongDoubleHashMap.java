package gnu.trove.map.hash;

//import gnu.trove.c_ref.U_ref;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.c;
import gnu.trove.a_ref.a;
import gnu.trove.b_ref.b;
import gnu.trove.ba.U_ref;
import gnu.trove.c_ref.X_ref;
import gnu.trove.e_ref.V_ref;
import gnu.trove.e_ref.aa;
import gnu.trove.e_ref.z;
import gnu.trove.f_ref.f;
import gnu.trove.map.Q_ref;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TLongDoubleHashMap extends U_ref implements Q_ref, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient double[] _values;

   public TLongDoubleHashMap() {
   }

   public TLongDoubleHashMap(int var1) {
      super(var1);
   }

   public TLongDoubleHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TLongDoubleHashMap(int var1, float var2, long var3, double var5) {
      super(var1, var2, var3, var5);
   }

   public TLongDoubleHashMap(long[] var1, double[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TLongDoubleHashMap(Q_ref var1) {
      super(var1.size());
      if (var1 instanceof TLongDoubleHashMap) {
         TLongDoubleHashMap var2 = (TLongDoubleHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0L) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new double[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      long[] var3 = this._set;
      double[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new long[var1];
      this._values = new double[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var5[var1] == 1) {
            long var7 = var3[var1];
            var2 = this.insertKey(var7);
            this._values[var2] = var4[var1];
         }
      }

   }

   public double put(long var1, double var3) {
      int var5 = this.insertKey(var1);
      return this.doPut(var1, var3, var5);
   }

   public double putIfAbsent(long var1, double var3) {
      int var5;
      return (var5 = this.insertKey(var1)) < 0 ? this._values[-var5 - 1] : this.doPut(var1, var3, var5);
   }

   private double doPut(long var1, double var3, int var5) {
      double var6 = this.no_entry_value;
      boolean var8 = true;
      if (var5 < 0) {
         var5 = -var5 - 1;
         var6 = this._values[var5];
         var8 = false;
      }

      this._values[var5] = var3;
      if (var8) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var6;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Long)var3.getKey(), (Double)var3.getValue());
      }

   }

   public void putAll(Q_ref var1) {
      this.ensureCapacity(var1.size());
      X_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public double get(long var1) {
      int var3;
      return (var3 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var3];
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

   public double remove(long var1) {
      double var3 = this.no_entry_value;
      int var5;
      if ((var5 = this.index(var1)) >= 0) {
         var3 = this._values[var5];
         this.removeAt(var5);
      }

      return var3;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public f keySet() {
      return new TLongDoubleHashMap$TKeyView(this);
   }

   public long[] keys() {
      long[] var1 = new long[this.size()];
      long[] var2 = this._set;
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

   public long[] keys(long[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new long[var2];
      }

      long[] var6 = this._set;
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

   public c valueCollection() {
      return new TLongDoubleHashMap$TValueView(this);
   }

   public double[] values() {
      double[] var1 = new double[this.size()];
      double[] var2 = this._values;
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

   public double[] values(double[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new double[var2];
      }

      double[] var6 = this._values;
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

   public boolean containsValue(double var1) {
      byte[] var3 = this._states;
      double[] var4;
      int var5 = (var4 = this._values).length;

      do {
         if (var5-- <= 0) {
            return false;
         }
      } while(var3[var5] != 1 || var1 != var4[var5]);

      return true;
   }

   public boolean containsKey(long var1) {
      return this.contains(var1);
   }

   public X_ref iterator() {
      return new TLongDoubleHashMap$TLongDoubleHashIterator(this, this);
   }

   public boolean forEachKey(aa var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(z var1) {
      byte[] var2 = this._states;
      double[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(V_ref var1) {
      byte[] var2 = this._states;
      long[] var3 = this._set;
      double[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues$478a2b8e(a var1) {
      byte[] var2 = this._states;
      double[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.c();
         }
      }

   }

   public boolean retainEntries(V_ref var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      long[] var4 = this._set;
      double[] var5 = this._values;
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

   public boolean increment(long var1) {
      return this.adjustValue(var1, 1.0D);
   }

   public boolean adjustValue(long var1, double var3) {
      int var5;
      if ((var5 = this.index(var1)) < 0) {
         return false;
      } else {
         double[] var10000 = this._values;
         var10000[var5] += var3;
         return true;
      }
   }

   public double adjustOrPutValue(long var1, double var3, double var5) {
      double var9;
      int var11;
      boolean var12;
      if ((var11 = this.insertKey(var1)) < 0) {
         var11 = -var11 - 1;
         double[] var10000 = this._values;
         var9 = var10000[var11] += var3;
         var12 = false;
      } else {
         var9 = this._values[var11] = var5;
         var12 = true;
      }

      if (var12) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var9;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Q_ref)) {
         return false;
      } else {
         Q_ref var16;
         if ((var16 = (Q_ref)var1).size() != this.size()) {
            return false;
         } else {
            double[] var2 = this._values;
            byte[] var3 = this._states;
            double var5 = this.getNoEntryValue();
            double var7 = var16.getNoEntryValue();
            int var4 = var2.length;

            while(var4-- > 0) {
               if (var3[var4] == 1) {
                  long var10 = this._set[var4];
                  double var12 = var16.get(var10);
                  double var14;
                  if ((var14 = var2[var4]) != var12 && var14 != var5 && var12 != var7) {
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
      this.forEachEntry(new TLongDoubleHashMap$1(this, var1));
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
            var1.writeLong(this._set[var2]);
            var1.writeDouble(this._values[var2]);
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
         long var3 = var1.readLong();
         double var5 = var1.readDouble();
         this.put(var3, var5);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   // $FF: synthetic method
   static long access$0(TLongDoubleHashMap var0) {
      return var0.no_entry_key;
   }

   // $FF: synthetic method
   static int access$1(TLongDoubleHashMap var0) {
      return var0._size;
   }

   // $FF: synthetic method
   static double access$2(TLongDoubleHashMap var0) {
      return var0.no_entry_value;
   }
}
