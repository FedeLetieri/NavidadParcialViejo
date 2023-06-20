import java.time.DayOfWeek
import java.time.LocalDate




open abstract class Regalo(
    val nombre: String,
    val marca:String,
    val costo:Int,
    val fechaLanzamientoMercado:LocalDate,
    val pais:String,
){
    var entregado:Boolean = false

    companion object{

        var id:Int = 0
        val MinimoRegaloValioso:Int =  5000
    }

    fun cambiarAEntregado(){
        entregado=true}

    fun estaEntregado() = entregado

    fun obtenerId():Int = id

    fun aumentarId(){id+=1}

    fun costoMayor() = costo>MinimoRegaloValioso

    fun esValioso():Boolean = costoMayor() && condicionParaSerValioso()

    abstract fun condicionParaSerValioso():Boolean
}

class Ropa(nombre: String,marca: String, costo: Int, fechaLanzamientoMercado: LocalDate, pais: String): Regalo(nombre,marca, costo, fechaLanzamientoMercado,
    pais
) {
    override fun condicionParaSerValioso(): Boolean = marcaFavorita("Jordache") || marcaFavorita("Charro") || marcaFavorita("Lee") || marcaFavorita("Motor Oil")

    fun marcaFavorita(marcaFavorita: String) = marca.equals(marcaFavorita)
}

class Juguete(nombre: String,marca: String, costo: Int, fechaLanzamientoMercado: LocalDate, pais: String): Regalo(nombre,marca, costo,
    fechaLanzamientoMercado, pais
) {
    override fun condicionParaSerValioso(): Boolean = fechaLanzamientoMercado.year < 2000
}

class Perfume(nombre: String,marca: String, costo: Int, fechaLanzamientoMercado: LocalDate, pais: String,val esExtranjero:Boolean): Regalo(nombre,marca, costo,
    fechaLanzamientoMercado, pais
) {
    override fun condicionParaSerValioso(): Boolean = esExtranjero
}

class Experiencias(nombre: String,val fechaDeLaExperiencia:LocalDate, marca: String, costo: Int, fechaLanzamientoMercado: LocalDate,
                   pais: String
): Regalo(nombre,marca, costo,
    fechaLanzamientoMercado, pais
) {
    override fun condicionParaSerValioso(): Boolean = fechaDeLaExperiencia.dayOfWeek == DayOfWeek.FRIDAY
}


class Persona(
    val nombre:String,
    var personalidad:Personalidad,
    var regaloObtenido: Regalo,
    val direccion:String,
    val dni:Int
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

object exigentes:Personalidad{
    override fun leGustaElRegalo(regalo: Regalo): Boolean = regalo.esValioso()
}

class marqueras(var marcaPredilecta: String): Personalidad{
    override fun leGustaElRegalo(regalo: Regalo): Boolean = regalo.marca.equals(marcaPredilecta)

    fun cambiarMarcaPredilecta(nuevaMarca:String){marcaPredilecta=nuevaMarca}
}

class combineta(val listaDePersonalidad: MutableList<Personalidad>): Personalidad{
    override fun leGustaElRegalo(regalo: Regalo): Boolean = listaDePersonalidad.any{it -> it.leGustaElRegalo(regalo)}
}

class Proceso(
    val listaDeRegalos:MutableList<Regalo>,
    val listaDePersonas:MutableList<Persona>,

){
    val listaDeAcciones:MutableList<Accion> = mutableListOf()
    lateinit var regaloAdecuado:Regalo

    fun ejecutar(){
        listaDePersonas.forEach{asignarRegalo(it)}
    }

    fun regalosNoEntregado() = listaDeRegalos.filter { (!it.estaEntregado()) }


    fun asignarRegalo(persona:Persona){
        regaloAdecuado = regalosNoEntregado().find{persona.regaloAdecuado(it)}?:regaloEsNulo()
        regaloAdecuado.aumentarId()
        regaloAdecuado.cambiarAEntregado()

        personaRecibeRegalo(persona,regaloAdecuado)
        registrarRegalo(persona,regaloAdecuado)
    }

    fun regaloEsNulo():Regalo = Ropa("Voucher" ,"Papapp",2000,LocalDate.now(),"argentina")


    fun agregarAcciones(accion:Accion){listaDeAcciones.add(accion)}

    fun eliminarAccion(accion:Accion){listaDeAcciones.remove(accion)}   

    fun registrarRegalo(persona: Persona,regalo: Regalo){
        listaDeAcciones.forEach{it.realizarAccion(regalo,persona)}
    }

    fun personaRecibeRegalo(persona:Persona, regalo: Any){
        persona.recibirRegalo(regaloAdecuado)
    }
}

interface Accion{
    abstract fun realizarAccion(regalo: Regalo,persona: Persona)
}

class enviarMailPersona(val mailSender:MailSender): Accion{

    override fun realizarAccion(regalo: Regalo,persona: Persona) {
            mailSender.sendEmail(
                Mail(
                    to=persona,
                    content = "Su regalo es el siguiente ${regalo.nombre}"
            ))
    }
}

class informarRenoLoco(val mailSender: MailSender):Accion{
    override fun realizarAccion(regalo: Regalo,persona: Persona) {
        enviarMensaje(regalo,persona)
    }

    fun enviarMensaje(regalo:Regalo,persona: Persona){
        sistemaDeFletes.agregarEnvio(
            Mensaje(
                direccionCliente = persona.direccion,
                nombrePersona = persona.nombre,
                dniPersona = persona.dni,
                codigoRegalo = regalo.obtenerId()
            )
        )
    }
}

class superaMonto(): Accion{

    val montoASuperar:Int = 10000

    override fun realizarAccion(regalo: Regalo,persona: Persona) {
        if (regaloSuperaMonto(regalo)){
            persona.cambiaPersonalidad(Interesada(5000))
        }
    }
    fun regaloSuperaMonto(regalo: Regalo):Boolean = regalo.costo > montoASuperar
}


interface MailSender{
    fun sendEmail(mail: Mail)
}

data class Mail(
    val to:Persona,
    val content:String
)

data class Mensaje(
    val direccionCliente:String,
    val nombrePersona: String,
    val dniPersona:Int,
    val codigoRegalo:Int
)


object sistemaDeFletes{
    val mensajes:MutableList<Mensaje> = mutableListOf()

    fun agregarEnvio(mensaje:Mensaje){
        mensajes.add(mensaje)
    }
}
