import mill._, scalalib._

/** All chisel modules should extends this trait where the chisel dependency is
  * included.
  */
trait ChiselModule extends ScalaModule {
    def scalaVersion = "2.12.13"
    def fs2Version = "1.0.0"
    def catsVersion = "1.4.0"
    def catsEffectVersion = "1.0.0"

    override def ivyDeps = Agg(
        ivy"edu.berkeley.cs::chisel3:3.1.+",

        ivy"com.lihaoyi::sourcecode:0.1.4",               // expert println debugging
        ivy"com.lihaoyi::pprint:0.5.3",                   // pretty print for types and case classes
        ivy"com.lihaoyi::fansi:0.2.5",
        ivy"org.typelevel::cats-core:1.4.0",          // abstract category dork stuff
        ivy"org.typelevel::cats-effect:1.0.0",  // IO monad category wank
        ivy"com.chuusai::shapeless:2.3.2",                // Abstract level category dork stuff
        ivy"org.tpolecat::atto-core:0.6.3",            // For parsing asm
        ivy"org.tpolecat::atto-refined:0.6.3",            // For parsing asm
        ivy"com.github.pathikrit::better-files:3.7.0",
        ivy"org.atnos::eff:5.2.0",
        ivy"com.slamdata::matryoshka-core:0.21.3"
    )

    override def scalacOptions = Seq(
      "-language:reflectiveCalls",
      "-Ypartial-unification",
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    )

    override def scalacPluginIvyDeps = Agg(
      ivy"org.scalamacros:::paradise:2.1.1",
      ivy"com.olegpy::better-monadic-for:0.2.4",
      ivy"org.spire-math::kind-projector:0.9.7",
    //   ivy"io.tryp:::splain:0.4.+"
      )
      
      object test extends Tests {
          override def ivyDeps = Agg(
            ivy"edu.berkeley.cs::chisel-iotesters:1.2.+",
        )
        def testFrameworks = Seq("org.scalatest.tools.Framework")
    }
}

object FiveStage extends ChiselModule {
}


