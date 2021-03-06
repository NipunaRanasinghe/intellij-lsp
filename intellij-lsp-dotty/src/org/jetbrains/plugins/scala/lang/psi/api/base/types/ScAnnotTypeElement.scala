package org.jetbrains.plugins.scala.lang.psi.api.base.types

import org.jetbrains.plugins.scala.lang.psi.types.result.TypeResult

/** 
* @author Alexander Podkhalyuzin
* Date: 07.03.2008
*/

trait ScAnnotTypeElement extends ScTypeElement {
  override protected val typeName = "TypeWithAnnotation"

  def typeElement: ScTypeElement = findChildByClassScala(classOf[ScTypeElement])

  protected def innerType: TypeResult = typeElement.`type`()
}