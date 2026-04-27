package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
class TFloatObjectHashMap$ValueView$1 extends TFloatObjectHashMap$ValueView$TFloatObjectValueHashIterator {
   // $FF: synthetic field
   final TFloatObjectHashMap$ValueView this$1;

   TFloatObjectHashMap$ValueView$1(TFloatObjectHashMap$ValueView var1, TFloatObjectHashMap$ValueView var2, TFloatObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TFloatObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
