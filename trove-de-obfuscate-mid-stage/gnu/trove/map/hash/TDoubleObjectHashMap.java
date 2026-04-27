package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleHash;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import gnu.trove.map.TDoubleObjectMap;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TDoubleObjectHashMap extends TDoubleHash implements TDoubleObjectMap, Externalizable {
   static final long serialVersionUID = 1L;
   private final TDoubleObjectProcedure PUT_ALL_PROC;
   protected transient Object[] _values;
   protected double no_entry_key;

   public TDoubleObjectHashMap() {
      this.PUT_ALL_PROC = new TDoubleObjectHashMap$1(this);
   }

   public TDoubleObjectHashMap(int var1) {
      super(var1);
      this.PUT_ALL_PROC = new TDoubleObjectHashMap$1(this);
      this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
   }

   public TDoubleObjectHashMap(int var1, float var2) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TDoubleObjectHashMap$1(this);
      this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
   }

   public TDoubleObjectHashMap(int var1, float var2, double var3) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TDoubleObjectHashMap$1(this);
      this.no_entry_key = var3;
   }

   public TDoubleObjectHashMap(TDoubleObjectMap var1) {
      this(var1.size(), 0.5F, var1.getNoEntryKey());
      this.putAll(var1);
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new Object[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      double[] var3 = this._set;
      Object[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new double[var1];
      this._values = new Object[var1];
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

   public double getNoEntryKey() {
      return this.no_entry_key;
   }

   public boolean containsKey(double var1) {
      return this.contains(var1);
   }

   public boolean containsValue(Object var1) {
      byte[] var2 = this._states;
      Object[] var3 = this._values;
      int var4;
      if (var1 == null) {
         var4 = var3.length;

         while(var4-- > 0) {
            if (var2[var4] == 1 && var3[var4] == null) {
               return true;
            }
         }
      } else {
         var4 = var3.length;

         while(var4-- > 0) {
            if (var2[var4] == 1 && (var1 == var3[var4] || var1.equals(var3[var4]))) {
               return true;
            }
         }
      }

      return false;
   }

   public Object get(double var1) {
      int var3;
      return (var3 = this.index(var1)) < 0 ? null : this._values[var3];
   }

   public Object put(double var1, Object var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var3, var4);
   }

   public Object putIfAbsent(double var1, Object var3) {
      int var4;
      return (var4 = this.insertKey(var1)) < 0 ? this._values[-var4 - 1] : this.doPut(var3, var4);
   }

   private Object doPut(Object var1, int var2) {
      Object var3 = null;
      boolean var4 = true;
      if (var2 < 0) {
         var2 = -var2 - 1;
         var3 = this._values[var2];
         var4 = false;
      }

      this._values[var2] = var1;
      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var3;
   }

   public Object remove(double var1) {
      Object var3 = null;
      int var4;
      if ((var4 = this.index(var1)) >= 0) {
         var3 = this._values[var4];
         this.removeAt(var4);
      }

      return var3;
   }

   protected void removeAt(int var1) {
      this._values[var1] = null;
      super.removeAt(var1);
   }

   public void putAll(Map var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Double)var3.getKey(), var3.getValue());
      }

   }

   public void putAll(TDoubleObjectMap var1) {
      var1.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, (Object)null);
   }

   public TDoubleSet keySet() {
      return new TDoubleObjectHashMap$KeyView(this);
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
      if (var1.length < this._size) {
         var1 = new double[this._size];
      }

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

   public Collection valueCollection() {
      return new TDoubleObjectHashMap$ValueView(this);
   }

   public Object[] values() {
      Object[] var1 = new Object[this.size()];
      Object[] var2 = this._values;
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

   public Object[] values(Object[] var1) {
      if (var1.length < this._size) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), this._size);
      }

      Object[] var2 = this._values;
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

   public TDoubleObjectIterator iterator() {
      return new TDoubleObjectHashMap$TDoubleObjectHashIterator(this, this);
   }

   public boolean forEachKey(TDoubleProcedure var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(gnu.trove.a_ref.a var1) {
      byte[] var2 = this._states;
      int var3 = this._values.length;

      do {
         if (var3-- <= 0) {
            return true;
         }
      } while(var2[var3] != 1 || var1.i());

      return false;
   }

   public boolean forEachEntry(TDoubleObjectProcedure var1) {
      byte[] var2 = this._states;
      double[] var3 = this._set;
      Object[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public boolean retainEntries(TDoubleObjectProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      double[] var4 = this._set;
      Object[] var5 = this._values;
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

   public void transformValues(gnu.trove.a_ref.a var1) {
      byte[] var2 = this._states;
      Object[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.g();
         }
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TDoubleObjectMap)) {
         return false;
      } else {
         TDoubleObjectMap var7;
         if ((var7 = (TDoubleObjectMap)var1).size() != this.size()) {
            return false;
         } else {
            try {
               TDoubleObjectIterator var2 = this.iterator();

               double var4;
               label38:
               do {
                  Object var3;
                  do {
                     if (!var2.hasNext()) {
                        return true;
                     }

                     var2.advance();
                     var4 = var2.key();
                     if ((var3 = var2.value()) == null) {
                        continue label38;
                     }
                  } while(var3.equals(var7.get(var4)));

                  return false;
               } while(var7.get(var4) == null && var7.containsKey(var4));

               return false;
            } catch (ClassCastException var6) {
               return true;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1 += HashFunctions.hash(this._set[var4]) ^ (var2[var4] == null ? 0 : var2[var4].hashCode());
         }
      }

      return var1;
   }

   public void writeExternal(ObjectOutput var1) {
   try {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeDouble(this.no_entry_key);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while(var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeDouble(this._set[var2]);
            var1.writeObject(this._values[var2]);
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
      this.no_entry_key = var1.readDouble();
      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         double var3 = var1.readDouble();
         Object var5 = var1.readObject();
         this.put(var3, var5);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   } catch (ClassNotFoundException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TDoubleObjectHashMap$2(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static int access$0(TDoubleObjectHashMap var0) {
      return var0._size;
   }
}
