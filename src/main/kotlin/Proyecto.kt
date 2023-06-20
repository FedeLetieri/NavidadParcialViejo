import java.time.LocalDate

open abstract class Regalo(
        val marca:String,
        val costo:Int,
        val fechaLanzamientoMercado:LocalDate,
        val pais:String
        ){

        companion object{
                val MinimoRegaloValioso:Int =  5000
        }

        fun costoMayor() = costo>MinimoRegaloValioso

        fun esValioso():Boolean = costoMayor() && condicionParaSerValioso()

        abstract fun condicionParaSerValioso():Boolean
}

class Ropa():Regalo{
        override fun condicionParaSerValioso(): Boolean = marcaFavorita("Jordache") || marcaFavorita("Charro") || marcaFavorita("Lee") || marcaFavorita("Motor Oil")

        fun marcaFavorita(marcaFavorita: String) = marca.equals(marcaFavorita)
}

class Juguete():Regalo{
        override fun condicionParaSerValioso(): Boolean = fechaLanzamientoMercado.Year < 2000
}

class Perfume():Regalo{
        override fun condicionParaSerValioso(): Boolean = true
}

class Experiencias(val fechaDeLaExperiencia:LocalDate):Regalo{
        override fun condicionParaSerValioso(): Boolean = fechaDeLaExperiencia.DayOfWeek == DayOfWeek.FRIDAY
}


class Persona(
        val nombre:String,
        var personalidad:Personalidad,
        var regaloObtenido: Regalo
){
        fun cambiaPersonalidad(personalidadNueva:Personalidad) {personalidad = personalidadNueva}

        fun regaloAdecuado(regalo: Regalo) = personalidad.leGustaElRegalo(regalo)

        fun recibirRegalo(regalo:Regalo){regaloObtenido=regalo}
}

interface Personalidad{
        abstract fun leGustaElRegalo(regalo:Regalo): Boolean
}

object Conformista: Personalidad{
        override fun leGustaElRegalo(regalo: Regalo): Boolean = true
}

class Interesada(val cantidadDeDineroMinima: Int): Personalidad{
        override fun leGustaElRegalo(regalo: Regalo): Boolean = regalo.costo > cantidadDeDineroMinima
}

class marqueras(val marcaPredilecta: String): Personalidad{
        override fun leGustaElRegalo(regalo: Regalo): Boolean = regalo.marca.equals(marcaPredilecta)

        fun cambiarMarcaPredilecta(nuevaMarca:String){marcaPredilecta=nuevaMarca}
}

class combineta(val listaDePersonalidad: MutableList<Personalidad>): Personalidad{
        override fun leGustaElRegalo(regalo: Regalo): Boolean = listaDePersonalidad.any{it -> it.leGustaElRegalo(regalo:Regalo)}
}

class Proceso(
        val listaDeRegalos:MutableList<Regalo>,
        val listaDePersonas:MutableList<Persona>,
        val listaDeAcciones:MutableList<Acciones>
){

        fun ejecutar(){
                listaDePersonas.forEach{it -> it.asignarRegalo(it)}
        }

        fun asignarRegalo(persona:Persona){
                val regaloAdecuado = listaDeRegalos.find(persona.regaloAdecuado(it))

                personaRecibeRegalo(persona,regaloAdecuado)
                registrarRegalo(persona,regaloAdecuado)
        }

        fun agregarAcciones(accion:Accion){listaDeAcciones.add(accion)}

        fun eliminarAccion(accion:Accion){listaDeAcciones.remove(accion)}

        fun registrarRegalo(persona: Persona,regalo: Regalo){
                listaDeAcciones.forEach{it.realizarAccion(regalo,persona)}
        }

        fun personaRecibeRegalo(persona:Persona,regalo:Regalo){
                persona.recibirRegalo(regaloAdecuado)
                listaDeRegalos.remove(regalo)
        }
}

interface Accion{
        abstract fun realizarAccion(regalo: Regalo,persona: Persona)
}

class enviarMailPersona(val mailSender:MailSender): Accion{

        override fun realizarAccion(regalo: Regalo) {
                mailSender.sendEmail(persona)
        }
}

class informarRenoLoco():Accion{
        override fun realizarAccion(regalo: Regalo,persona: Persona) {

        }
}

class superaMonto(): Accion{
        override fun realizarAccion(regalo: Regalo,persona: Persona) {
                if (regaloSuperaMonto()){
                        persona.cambiarPersonalidad(Interesada(5000))
                }
        }
}


object MailSender{
        fun sendEmail(persona: Persona){}
}

data class mail(
        val to:Persona,
        val content:String
)

