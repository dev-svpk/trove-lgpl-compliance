package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.TFloatCollection;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TObjectFloatHashMap extends TObjectHash implements gnu.trove.map.TObjectFloatMap, Externalizable {
   static final long serialVersionUID = 1L;
   private final TObjectFloatProcedure PUT_ALL_PROC;
   protected transient float[] _values;
   protected float no_entry_value;

   public TObjectFloatHashMap() {
      this.PUT_ALL_PROC = new TObjectFloatHashMap$1(this);
      this.no_entry_value = Constants.f;
   }

   public TObjectFloatHashMap(int var1) {
      super(var1);
      this.PUT_ALL_PROC = new TObjectFloatHashMap$1(this);
      this.no_entry_value = Constants.f;
   }

   public TObjectFloatHashMap(int var1, float var2) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TObjectFloatHashMap$1(this);
      this.no_entry_value = Constants.f;
   }

   public TObjectFloatHashMap(int var1, float var2, float var3) {
      super(var1, var2);
      this.PUT_ALL_PROC = new TObjectFloatHashMap$1(this);
      this.no_entry_value = var3;
      if (this.no_entry_value != 0.0F) {
         Arrays.fill(this._values, this.no_entry_value);
      }

   }

   public TObjectFloatHashMap(gnu.trove.map.TObjectFloatMap var1) {
      this(var1.size(), 0.5F, var1.getNoEntryValue());
      if (var1 instanceof TObjectFloatHashMap) {
         TObjectFloatHashMap var2 = (TObjectFloatHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_value != 0.0F) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   public int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new float[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      Object[] var3 = this._set;
      float[] var4 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = new float[var1];
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

   public float getNoEntryValue() {
      return this.no_entry_value;
   }

   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   public boolean containsValue(float var1) {
      Object[] var2 = this._set;
      float[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1 != var3[var4]);

      return true;
   }

   public float get(Object var1) {
      int var2;
      return (var2 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var2];
   }

   public float put(Object var1, float var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var2, var3);
   }

   public float putIfAbsent(Object var1, float var2) {
      int var3;
      return (var3 = this.insertKey(var1)) < 0 ? this._values[-var3 - 1] : this.doPut(var2, var3);
   }

   private float doPut(float var1, int var2) {
      float var3 = this.no_entry_value;
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

   public float remove(Object var1) {
      float var2 = this.no_entry_value;
      int var3;
      if ((var3 = this.index(var1)) >= 0) {
         var2 = this._values[var3];
         this.removeAt(var3);
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
         this.put(var3.getKey(), (Float)var3.getValue());
      }

   }

   public void putAll(gnu.trove.map.TObjectFloatMap var1) {
      var1.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, FREE);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
   }

   public Set keySet() {
      return new TObjectFloatHashMap$KeyView(this);
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

   public TFloatCollection valueCollection() {
      return new TObjectFloatHashMap$TFloatValueCollection(this);
   }

   public float[] values() {
      float[] var1 = new float[this.size()];
      float[] var2 = this._values;
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

   public float[] values(float[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new float[var2];
      }

      float[] var3 = this._values;
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

   public TObjectFloatIterator iterator() {
      return new TObjectFloatHashMap$TObjectFloatHashIterator(this, this);
   }

   public boolean increment(Object var1) {
      return this.adjustValue(var1, 1.0F);
   }

   public boolean adjustValue(Object var1, float var2) {
      int var3;
      if ((var3 = this.index(var1)) < 0) {
         return false;
      } else {
         float[] var10000 = this._values;
         var10000[var3] += var2;
         return true;
      }
   }

   public float adjustOrPutValue(Object var1, float var2, float var3) {
      int var4;
      boolean var5;
      if ((var4 = this.insertKey(var1)) < 0) {
         var4 = -var4 - 1;
         float[] var10000 = this._values;
         var2 = var10000[var4] += var2;
         var5 = false;
      } else {
         var2 = this._values[var4] = var3;
         var5 = true;
      }

      if (var5) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var2;
   }

   public boolean forEachKey(gnu.trove.a_ref.a var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(TFloatProcedure var1) {
      Object[] var2 = this._set;
      float[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(TObjectFloatProcedure var1) {
      Object[] var2 = this._set;
      float[] var3 = this._values;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1.execute(var2[var4], var3[var4]));

      return false;
   }

   public boolean retainEntries(TObjectFloatProcedure var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      float[] var4 = this._values;
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

   public void transformValues(gnu.trove.a_ref.a var1) {
      Object[] var2 = this._set;
      float[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] != null && var2[var4] != REMOVED) {
            var3[var4] = var1.d();
         }
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof gnu.trove.map.TObjectFloatMap)) {
         return false;
      } else {
         gnu.trove.map.TObjectFloatMap var6;
         if ((var6 = (gnu.trove.map.TObjectFloatMap)var1).size() != this.size()) {
            return false;
         } else {
            try {
               TObjectFloatIterator var2 = this.iterator();

               Object var3;
               label38:
               do {
                  float var4;
                  do {
                     if (!var2.hasNext()) {
                        return true;
                     }

                     var2.advance();
                     var3 = var2.key();
                     if ((var4 = var2.value()) == this.no_entry_value) {
                        continue label38;
                     }
                  } while(var4 == var6.get(var3));

                  return false;
               } while(var6.get(var3) == var6.getNoEntryValue() && var6.containsKey(var3));

               return false;
            } catch (ClassCastException var5) {
               return true;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._set;
      float[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED) {
            var1 += HashFunctions.hash(var3[var4]) ^ (var2[var4] == null ? 0 : var2[var4].hashCode());
         }
      }

      return var1;
   }

   public void writeExternal(ObjectOutput var1) {
   try {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeFloat(this.no_entry_value);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while(var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
            var1.writeFloat(this._values[var2]);
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
      this.no_entry_value = var1.readFloat();
      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         Object var3 = var1.readObject();
         float var4 = var1.readFloat();
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
      this.forEachEntry(new TObjectFloatHashMap$2(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static int access$0(TObjectFloatHashMap var0) {
      return var0._size;
   }
}
