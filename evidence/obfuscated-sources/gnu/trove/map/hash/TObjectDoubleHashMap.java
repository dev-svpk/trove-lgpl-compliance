package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.c;
import gnu.trove.b_ref.a;
import gnu.trove.b_ref.b;
import gnu.trove.ba.aa;
import gnu.trove.c_ref.ag;
import gnu.trove.e_ref.ae;
import gnu.trove.e_ref.z;
import gnu.trove.map.Z_ref;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TObjectDoubleHashMap extends aa implements Z_ref, Externalizable {
   static final long serialVersionUID = 1L;
   private final ae PUT_ALL_PROC;
   protected transient double[] _values;
   protected double no_entry_value;

   public TObjectDoubleHashMap() {
      this.PUT_ALL_PROC = new TObjectDoubleHashMap$1(this);
      this.no_entry_value = a.g;
   }

   public TObjectDoubleHashMap(int var1) {
      super(var1);
      this.PUT_ALL_PROC = new TObjectDoubleHashMap$1(this);
      this.no_entry_value = a.g;
   }

   public TObjectDoubleHashMap(int var1, float var2) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TObjectDoubleHashMap$1(this);
      this.no_entry_value = a.g;
   }

   public TObjectDoubleHashMap(int var1, float var2, double var3) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TObjectDoubleHashMap$1(this);
      this.no_entry_value = var3;
      if (this.no_entry_value != 0.0D) {
         Arrays.fill(this._values, this.no_entry_value);
      }

   }

   public TObjectDoubleHashMap(Z_ref var1) {
      this(var1.size(), 0.5F, var1.getNoEntryValue());
      if (var1 instanceof TObjectDoubleHashMap) {
         TObjectDoubleHashMap var2 = (TObjectDoubleHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   public int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new double[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      Object[] var3 = this._set;
      double[] var4 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = new double[var1];
      Arrays.fill(this._values, this.no_entry_value);
      var1 = var2;

      while(var1-- > 0) {
         if (var3[var1] != FREE && var3[var1] != REMOVED) {
            Object var6 = var3[var1];
            int var5;
            if ((var5 = this.insertKey(var6)) < 0) {
               this.throwObjectContractViolation(this._set[-var5 - 1], var6);
            }

            this._set[var5] = var6;
            this._values[var5] = var4[var1];
         }
      }

   }

   public double getNoEntryValue() {
      return this.no_entry_value;
   }

   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   public boolean containsValue(double var1) {
      Object[] var3 = this._set;
      double[] var4;
      int var5 = (var4 = this._values).length;

      do {
         if (var5-- <= 0) {
            return false;
         }
      } while(var3[var5] == FREE || var3[var5] == REMOVED || var1 != var4[var5]);

      return true;
   }

   public double get(Object var1) {
      int var2;
      return (var2 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var2];
   }

   public double put(Object var1, double var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var2, var4);
   }

   public double putIfAbsent(Object var1, double var2) {
      int var4;
      return (var4 = this.insertKey(var1)) < 0 ? this._values[-var4 - 1] : this.doPut(var2, var4);
   }

   private double doPut(double var1, int var3) {
      double var4 = this.no_entry_value;
      boolean var6 = true;
      if (var3 < 0) {
         var3 = -var3 - 1;
         var4 = this._values[var3];
         var6 = false;
      }

      this._values[var3] = var1;
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var4;
   }

   public double remove(Object var1) {
      double var2 = this.no_entry_value;
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

   public void putAll(Map var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put(var3.getKey(), (Double)var3.getValue());
      }

   }

   public void putAll(Z_ref var1) {
      var1.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, FREE);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
   }

   public Set keySet() {
      return new TObjectDoubleHashMap$KeyView(this);
   }

   public Object[] keys() {
      Object[] var1 = new Object[this.size()];
      Object[] var2;
      int var3 = (var2 = this._set).length;
      int var4 = 0;

      while(var3-- > 0) {
         if (var2[var3] != FREE && var2[var3] != REMOVED) {
            var1[var4++] = var2[var3];
         }
      }

      return var1;
   }

   public Object[] keys(Object[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
      }

      Object[] var5;
      int var3 = (var5 = this._set).length;
      int var4 = 0;

      while(var3-- > 0) {
         if (var5[var3] != FREE && var5[var3] != REMOVED) {
            var1[var4++] = var5[var3];
         }
      }

      return var1;
   }

   public c valueCollection() {
      return new TObjectDoubleHashMap$TDoubleValueCollection(this);
   }

   public double[] values() {
      double[] var1 = new double[this.size()];
      double[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED) {
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

      double[] var3 = this._values;
      Object[] var4 = this._set;
      int var5 = var3.length;
      int var6 = 0;

      while(var5-- > 0) {
         if (var4[var5] != FREE && var4[var5] != REMOVED) {
            var1[var6++] = var3[var5];
         }
      }

      if (var1.length > var2) {
         var1[var2] = this.no_entry_value;
      }

      return var1;
   }

   public ag iterator() {
      return new TObjectDoubleHashMap$TObjectDoubleHashIterator(this, this);
   }

   public boolean increment(Object var1) {
      return this.adjustValue(var1, 1.0D);
   }

   public boolean adjustValue(Object var1, double var2) {
      int var4;
      if ((var4 = this.index(var1)) < 0) {
         return false;
      } else {
         double[] var10000 = this._values;
         var10000[var4] += var2;
         return true;
      }
   }

   public double adjustOrPutValue(Object var1, double var2, double var4) {
      double var8;
      int var10;
      boolean var11;
      if ((var10 = this.insertKey(var1)) < 0) {
         var10 = -var10 - 1;
         double[] var10000 = this._values;
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

   public boolean forEachKey$1d6e2644(gnu.trove.a_ref.a var1) {
      return this.forEach$1d6e2644(var1);
   }

   public boolean forEachValue(z var1) {
      Object[] var2 = this._set;
      double[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(ae var1) {
      Object[] var2 = this._set;
      double[] var3 = this._values;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1.execute(var2[var4], var3[var4]));

      return false;
   }

   public boolean retainEntries(ae var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      double[] var4 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var5 = var3.length;

         while(var5-- > 0) {
            if (var3[var5] != FREE && var3[var5] != REMOVED && !var1.execute(var3[var5], var4[var5])) {
               this.removeAt(var5);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   public void transformValues$478a2b8e(gnu.trove.a_ref.a var1) {
      Object[] var2 = this._set;
      double[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] != null && var2[var4] != REMOVED) {
            var3[var4] = var1.c();
         }
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Z_ref)) {
         return false;
      } else {
         Z_ref var8;
         if ((var8 = (Z_ref)var1).size() != this.size()) {
            return false;
         } else {
            try {
               ag var2 = this.iterator();

               Object var3;
               label38:
               do {
                  double var5;
                  do {
                     if (!var2.hasNext()) {
                        return true;
                     }

                     var2.advance();
                     var3 = var2.key();
                     if ((var5 = var2.value()) == this.no_entry_value) {
                        continue label38;
                     }
                  } while(var5 == var8.get(var3));

                  return false;
               } while(var8.get(var3) == var8.getNoEntryValue() && var8.containsKey(var3));

               return false;
            } catch (ClassCastException var7) {
               return true;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._set;
      double[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED) {
            var1 += b.a(var3[var4]) ^ (var2[var4] == null ? 0 : var2[var4].hashCode());
         }
      }

      return var1;
   }

   public void writeExternal(ObjectOutput var1) {
   try {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeDouble(this.no_entry_value);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while(var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
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
      this.no_entry_value = var1.readDouble();
      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         Object var3 = var1.readObject();
         double var4 = var1.readDouble();
         this.put(var3, var4);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   } catch (ClassNotFoundException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TObjectDoubleHashMap$2(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static int access$0(TObjectDoubleHashMap var0) {
      return var0._size;
   }
}
