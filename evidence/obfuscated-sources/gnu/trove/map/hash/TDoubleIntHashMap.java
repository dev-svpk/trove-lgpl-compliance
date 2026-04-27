package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.e;
import gnu.trove.a_ref.a;
import gnu.trove.b_ref.b;
import gnu.trove.ba.w;
import gnu.trove.c_ref.x;
import gnu.trove.e_ref.R_ref;
import gnu.trove.e_ref.z;
import gnu.trove.f_ref.c;
import gnu.trove.map.u;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TDoubleIntHashMap extends w implements u, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient int[] _values;

   public TDoubleIntHashMap() {
   }

   public TDoubleIntHashMap(int var1) {
      super(var1);
   }

   public TDoubleIntHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TDoubleIntHashMap(int var1, float var2, double var3, int var5) {
      super(var1, var2, var3, var5);
   }

   public TDoubleIntHashMap(double[] var1, int[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TDoubleIntHashMap(u var1) {
      super(var1.size());
      if (var1 instanceof TDoubleIntHashMap) {
         TDoubleIntHashMap var2 = (TDoubleIntHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0.0D) {
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
      this._values = new int[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      double[] var3 = this._set;
      int[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new double[var1];
      this._values = new int[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var5[var1] == 1) {
            double var7 = var3[var1];
            var2 = this.insertKey(var7);
            this._values[var2] = var4[var1];
         }
      }

   }

   public int put(double var1, int var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var3, var4);
   }

   public int putIfAbsent(double var1, int var3) {
      int var4;
      return (var4 = this.insertKey(var1)) < 0 ? this._values[-var4 - 1] : this.doPut(var1, var3, var4);
   }

   private int doPut(double var1, int var3, int var4) {
      int var5 = this.no_entry_value;
      boolean var2 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var2 = false;
      }

      this._values[var4] = var3;
      if (var2) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Double)var3.getKey(), (Integer)var3.getValue());
      }

   }

   public void putAll(u var1) {
      this.ensureCapacity(var1.size());
      x var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public int get(double var1) {
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

   public int remove(double var1) {
      int var3 = this.no_entry_value;
      int var4;
      if ((var4 = this.index(var1)) >= 0) {
         var3 = this._values[var4];
         this.removeAt(var4);
      }

      return var3;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public c keySet() {
      return new TDoubleIntHashMap$TKeyView(this);
   }

   public double[] keys() {
      double[] var1 = new double[this.size()];
      double[] var2 = this._set;
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

   public double[] keys(double[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new double[var2];
      }

      double[] var6 = this._set;
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

   public e valueCollection() {
      return new TDoubleIntHashMap$TValueView(this);
   }

   public int[] values() {
      int[] var1 = new int[this.size()];
      int[] var2 = this._values;
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

   public int[] values(int[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new int[var2];
      }

      int[] var6 = this._values;
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

   public boolean containsValue(int var1) {
      byte[] var2 = this._states;
      int[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var2[var4] != 1 || var1 != var3[var4]);

      return true;
   }

   public boolean containsKey(double var1) {
      return this.contains(var1);
   }

   public x iterator() {
      return new TDoubleIntHashMap$TDoubleIntHashIterator(this, this);
   }

   public boolean forEachKey(z var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(R_ref var1) {
      byte[] var2 = this._states;
      int[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(gnu.trove.e_ref.w var1) {
      byte[] var2 = this._states;
      double[] var3 = this._set;
      int[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues$637dbeb2(a var1) {
      byte[] var2 = this._states;
      int[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.e();
         }
      }

   }

   public boolean retainEntries(gnu.trove.e_ref.w var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      double[] var4 = this._set;
      int[] var5 = this._values;
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

   public boolean increment(double var1) {
      return this.adjustValue(var1, 1);
   }

   public boolean adjustValue(double var1, int var3) {
      int var4;
      if ((var4 = this.index(var1)) < 0) {
         return false;
      } else {
         int[] var10000 = this._values;
         var10000[var4] += var3;
         return true;
      }
   }

   public int adjustOrPutValue(double var1, int var3, int var4) {
      int var2;
      int var5;
      boolean var6;
      if ((var5 = this.insertKey(var1)) < 0) {
         var5 = -var5 - 1;
         int[] var10000 = this._values;
         var2 = var10000[var5] += var3;
         var6 = false;
      } else {
         var2 = this._values[var5] = var4;
         var6 = true;
      }

      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof u)) {
         return false;
      } else {
         u var10;
         if ((var10 = (u)var1).size() != this.size()) {
            return false;
         } else {
            int[] var2 = this._values;
            byte[] var3 = this._states;
            int var4 = this.getNoEntryValue();
            int var5 = var10.getNoEntryValue();
            int var6 = var2.length;

            while(var6-- > 0) {
               if (var3[var6] == 1) {
                  double var8 = this._set[var6];
                  int var7 = var10.get(var8);
                  int var11;
                  if ((var11 = var2[var6]) != var7 && var11 != var4 && var7 != var5) {
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
      this.forEachEntry(new TDoubleIntHashMap$1(this, var1));
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
            var1.writeDouble(this._set[var2]);
            var1.writeInt(this._values[var2]);
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
         double var3 = var1.readDouble();
         int var5 = var1.readInt();
         this.put(var3, var5);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   // $FF: synthetic method
   static double access$0(TDoubleIntHashMap var0) {
      return var0.no_entry_key;
   }

   // $FF: synthetic method
   static int access$1(TDoubleIntHashMap var0) {
      return var0._size;
   }

   // $FF: synthetic method
   static int access$2(TDoubleIntHashMap var0) {
      return var0.no_entry_value;
   }
}
