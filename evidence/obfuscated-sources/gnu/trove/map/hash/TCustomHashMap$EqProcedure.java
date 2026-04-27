package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ai;
import java.util.Map;

final class TCustomHashMap$EqProcedure implements ai {
   private final Map _otherMap;

   TCustomHashMap$EqProcedure(Map var1) {
      this._otherMap = var1;
   }

   public final boolean execute(Object var1, Object var2) {
      if (var2 == null && !this._otherMap.containsKey(var1)) {
         return false;
      } else {
         return (var1 = this._otherMap.get(var1)) == var2 || var1 != null && var1.equals(var2);
      }
   }
}
