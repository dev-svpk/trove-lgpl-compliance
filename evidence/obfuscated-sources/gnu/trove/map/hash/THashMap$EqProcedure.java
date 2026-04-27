package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ai;
import java.util.Map;

final class THashMap$EqProcedure implements ai {
   private final Map _otherMap;
   // $FF: synthetic field
   final THashMap this$0;

   THashMap$EqProcedure(THashMap var1, Map var2) {
      this.this$0 = var1;
      this._otherMap = var2;
   }

   public final boolean execute(Object var1, Object var2) {
      if (var2 == null && !this._otherMap.containsKey(var1)) {
         return false;
      } else {
         return (var1 = this._otherMap.get(var1)) == var2 || var1 != null && THashMap.access$0(this.this$0, var1, var2);
      }
   }
}
