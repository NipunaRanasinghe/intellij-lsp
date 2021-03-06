package org.jetbrains.plugins.scala.lang.psi.api.toplevel

import com.intellij.psi._
import org.jetbrains.plugins.scala.extensions.StubBasedExt
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiElement
import org.jetbrains.plugins.scala.lang.psi.adapters.PsiModifierListOwnerAdapter
import org.jetbrains.plugins.scala.lang.psi.api.base._
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory
import org.jetbrains.plugins.scala.macroAnnotations.{Cached, ModCount}

/**
* @author ilyas
*/

trait ScModifierListOwner extends ScalaPsiElement with PsiModifierListOwnerAdapter {

  @Cached(ModCount.anyScalaPsiModificationCount, this)
  override def getModifierList: ScModifierList = {
    val child = this.stubOrPsiChild(ScalaElementTypes.MODIFIERS)
    child.getOrElse(ScalaPsiElementFactory.createEmptyModifierList(this))
  }

  def hasModifierProperty(name: String): Boolean = hasModifierPropertyInner(name)

  def hasModifierPropertyScala(name: String): Boolean = {
    if (name == PsiModifier.PUBLIC)
      !hasModifierPropertyScala("private") && !hasModifierPropertyScala("protected")
    else
      hasModifierPropertyInner(name)
  }

  def hasAbstractModifier: Boolean = hasModifierPropertyInner("abstract")

  def hasFinalModifier: Boolean = hasModifierPropertyInner("final")

  private def hasModifierPropertyInner(name: String): Boolean =
    Option(getModifierList).exists(_.hasModifierProperty(name))

  def setModifierProperty(name: String, value: Boolean) {
    Option(getModifierList).foreach(_.setModifierProperty(name, value))
  }
}