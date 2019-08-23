<template>
  <el-container>
    <el-header>
      <span>Nueva Solicitud</span>
    </el-header>
    <el-main>
      <el-form
        ref="form"
        :model="form"
        label-position="top">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" title="INFORMACIÓN INTERNA OFICINA">
            <el-row :gutter="4">
              <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                <el-form-item prop="oficina" label="Centro de Costo">
                  <el-select v-model="form.solicitud.a.oficina" filterable style="width: 90%;">
                    <el-option v-for="o in oficinas" :key="o.id" :label="o.descripcion" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                <el-form-item prop="asesor" label="Asesor">
                  <el-select v-model="form.solicitud.a.id_asesor" filterable style="width: 90%;">
                    <el-option v-for="a in asesores" :key="a.id" :label="a.descripcion" :value="a.id" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item name="1" title="INFORMACIÓN DEL SOLICITANTE">
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item prop="id_identificacion" label="Tipo de Documento">
                  <el-select :disabled="es_bloqueo" v-model="form.solicitud.a.id_identificacion" filterable>
                    <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item prop="id_persona" label="Número de Documento">
                  <el-input :disabled="es_bloqueo" v-model="form.solicitud.a.id_persona" @blur="buscarPersona" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-form-item prop="id_persona" label="Nombre Completo">
                  <el-input v-model="nombre" readonly />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-button :disabled="form.solicitud.a.id_identificacion == null || form.solicitud.a.id_persona == null" type="success" icon="el-icon-edit" circle title="Actualizar Información de la Persona" @click="actualizarPersona(form.solicitud.a.id_identificacion, form.solicitud.a.id_persona)"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item v-if="solicitante_existe" name="2" title="INFORMACIÓN DE LA SOLICITUD">
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <el-form-item label="Fecha Recepcion">
                  <el-date-picker v-model="form.solicitud.a.fecha_recepcion" readonly style="width: 90%;"/>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item label="Valor Solicitado">
                  <el-input
                    v-currency="{locale, currency}"
                    v-model="form.solicitud.a.valor_solicitado" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item label="Tipo de Cuota">
                  <el-select v-model="form.solicitud.a.tipo_cuota" filterable>
                    <el-option v-for="t in tipos_cuota" :key="t.id" :label="t.descripcion" :value="t.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item label="Línea de Crédito">
                  <el-select v-model="form.solicitud.a.linea" filterable>
                    <el-option v-for="l in lineas" :key="l.id" :label="l.descripcion" :value="l.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item label="Tipo de Garantía">
                  <el-select v-model="form.solicitud.a.garantia" filterable>
                    <el-option v-for="g in tipos_garantia" :key="g.id" :label="g.descripcion" :value="g.id" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
                <el-form-item label="Descripción Garantía">
                  <el-input v-model="form.solicitud.a.descripcion_garantia" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item label="Respaldo">
                  <el-select v-model="form.solicitud.b.respaldo" filterable>
                    <el-option v-for="r in tipos_respaldo" :key="r.id" :label="r.descripcion" :value="r.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <el-form-item label="Amortización Capital">
                  <el-input v-model="form.solicitud.a.amortizacion" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <el-form-item label="Amortización Interés">
                  <el-input v-model="form.solicitud.b.pago_interes" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <el-form-item label="Plazo (En Días)">
                  <el-input v-model="form.solicitud.a.plazo" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-form-item label="Destino del Prestamo">
                  <el-input v-model="form.solicitud.a.destino" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-form-item label="Observaciones">
                  <el-input v-model="form.solicitud.b.observacion" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item v-if="solicitante_existe" name="3" title="CODEUDORES">
            <el-row>
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <span>Tipo de Documento</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <span>Número de Documento</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <span>Nombre Completo</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <span>Es Conyuge</span>
              </el-col>
            </el-row>
            <el-row v-for="c in form.codeudores" :key="c.consecutivo" :gutter="4">
              <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
                <el-form-item>
                  <el-select v-model="c.id_identificacion" filterable>
                    <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                <el-form-item>
                  <el-input v-model="c.id_persona" @blur="buscarCodeudor(c)" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="10" :lg="10" :xl="10">
                <el-form-item>
                  <el-input v-model="c.nombre" readonly />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-checkbox v-model="c.es_conyuge" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                <el-button :disabled="c.id_identificacion == null || c.id_persona == null" size="mini" type="success" icon="el-icon-edit" circle title="Actualizar Información del Codeudor" @click="actualizarPersona(c.id_identificacion, c.id_persona)"/>
              </el-col>
              <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                <el-button size="mini" type="danger" circle icon="el-icon-minus" title="Quitar Codeudor" @click="onRemoveCodeudor(c.consecutivo)" />
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="22">
                <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nuevo Codeudor" @click="onAddCodeudor()" />
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item v-if="solicitante_existe" name="4" title="INFORMACION CREDITICIA">
            <el-row>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <span>Colocación</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <span>Valor Inicial</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <span>Saldo</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <span>Cuota</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <span>Fecha Interés</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <span>Estado</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <span>Entidad</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <span>Fecha Vencimiento</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <span>Descontar</span>
              </el-col>
              <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                <span>Tipo</span>
              </el-col>
            </el-row>
            <el-row v-for="c in form.creditos" :key="c.consecutivo" :gutter="4" class="infocredito">
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-input v-model="c.id_colocacion" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <el-form-item>
                  <el-input v-currency="{locale, currency}" v-model="c.valor_inicial" style="text-align: right;"/>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <el-form-item>
                  <el-input v-currency="{locale, currency}" v-model="c.saldo" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-input v-currency="{locale, currency}" v-model="c.cuota_mensual" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <el-form-item>
                  <el-date-picker v-model="c.fecha_interes" style="width: 100%;" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-select v-model="c.estado">
                    <el-option v-for="o in tipos_estado_colocacion" :label="o.descripcion" :key="o.id" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <el-form-item>
                  <el-input v-model="c.entidad" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
                <el-form-item>
                  <el-date-picker v-model="c.vencimiento" style="width: 100%;" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-checkbox v-model="c.descontar" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="1" :lg="1" :xl="1">
                <el-form-item>
                  <span>{{ c.tipo }}</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="22">
                <el-button style="display: table-cell;" type="primary" size="mini" circle icon="el-icon-plus" title="Adicionar Nuevo Credito" @click="onAddCredito()" />
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <el-dialog
        :visible.sync="dialogPersonaVisible"
        :close="dialogoCerrado()"
        destroy-on-close
        class="pdialog"
        show-close>
        <PersonaComponent :id_identificacion="id_identificacion" :id_persona="id_persona" />
        <el-button type="primary" @click="dialogPersonaVisible = false" >Cerrar</el-button>
      </el-dialog>
    </el-main>
  </el-container>
</template>
<script>
import PersonaComponent from '@/components/Persona'
import { obtenerListaOficinas, obtenerListaAsesores,
  obtenerListaTipoIdentificacion, obtenerListaTipoCuota,
  obtenerListaLineaCredito, obtenerListaTipoGarantia,
  obtenerListaTipoRespaldo, obtenerListaTipoEstadoColocacion } from '@/api/tipos'
import { obtenerPersona } from '@/api/persona'
import { buscarCredito } from '@/api/infocredito'

export default {
  components: {
    PersonaComponent
  },
  data() {
    return {
      locale: 'en',
      currency: 'USD',
      activeNames: ['0', '1', '2', '3', '4', '5', '6'],
      dialogPersonaVisible: false,
      solicitanteNoValido: true,
      solicitante_existe: false,
      oficinas: [],
      asesores: [],
      tipo_documento: [],
      tipos_cuota: [],
      lineas: [],
      tipos_garantia: [],
      tipos_respaldo: [],
      tipos_estado_colocacion: [],
      id_identificacion: null,
      id_persona: null,
      nombre: null,
      es_bloqueo: false,
      codeudorNoValido: true,
      form: {
        solicitud: {
          a: {
            id_asesor: null,
            id_solicitud: null,
            id_identificacion: null,
            id_persona: null,
            valor_solicitado: null,
            plazo: null,
            amortizacion: null,
            garantia: null,
            tipo_cuota: null,
            estado: null,
            linea: null,
            fecha_recepcion: new Date(),
            oficina: null,
            destino: null,
            fecha_concepto: null,
            id_empleado: null,
            ente_aprobador: null,
            numero_acta: null,
            tasa_interes: null,
            plazo_aprobado: null,
            descripcion_garantia: null,
            numero_codeudores: null
          },
          b: {
            respaldo: null,
            valor_aprobado: null,
            id_analisis: null,
            ingresos: null,
            disponible: null,
            deducciones: null,
            valor_cuota: null,
            disponibilidad: null,
            solv_economica: null,
            exp_creditos: null,
            inversion: null,
            clasificacion: null,
            es_desembolso_parcial: null,
            pago_interes: null,
            id_copia: null,
            observacion: null,
            s_vida: null,
            s_exequial: null,
            es_fundacion: null,
            numero_riesgo: null,
            fecha_analisis: null
          }
        },
        codeudores: [],
        creditos: [],
        inmuebles: [],
        vehiculos: []
      },
      codeudor: {
        id_identificacion: null,
        id_persona: null,
        nombre: null,
        es_conyuge: null
      },
      credito: {
        entidad: null,
        valor_inicial: null,
        saldo: null,
        cuota_mensual: null,
        vencimiento: null,
        id_persona: null,
        id_identificacion: null,
        es_descuento: null,
        id_solicitud: null,
        id_colocacion: null,
        fecha_capital: null,
        fecha_interes: null,
        estado: null,
        consecutivo: null
      }
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
      obtenerListaTipoCuota().then(response => {
        this.tipos_cuota = response.data
        obtenerListaLineaCredito().then(response => {
          this.lineas = response.data
          obtenerListaTipoGarantia().then(response => {
            this.tipos_garantia = response.data
            obtenerListaTipoRespaldo().then(response => {
              this.tipos_respaldo = response.data
              obtenerListaOficinas().then(response => {
                this.oficinas = response.data
                obtenerListaAsesores().then(response => {
                  this.asesores = response.data
                  obtenerListaTipoEstadoColocacion().then(response => {
                    this.tipos_estado_colocacion = response.data
                  })
                }).catch(error => {
                  console.log('Error Consultando Asesores: ' + error)
                })
              }).catch(error => {
                console.log('Error Consultando Centros de costo: ' + error)
              })
            }).catch(error => {
              console.log('Error Consultando Tipos Respaldo: ' + error)
            })
          }).catch(error => {
            console.log('Error Consultando Tipos Garantia: ' + error)
          })
        }).catch(error => {
          console.log('Error Consultando Lineas de Credito :' + error)
        })
      }).catch(error => {
        console.log('Error consultando tipo cuota:' + error)
      })
    }).catch(error => {
      console.log('Error consultando tipo identificación:' + error)
    })
  },
  methods: {
    dialogoCerrado() {
      this.id_identificacion = null
      this.id_persona = null
      console.log('Se cerro el dialogo de persona')
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersona(this.form.solicitud.a.id_identificacion, this.form.solicitud.a.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.id_identificacion = response.data.a.id_identificacion
          this.id_persona = response.data.a.id_persona
          this.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
          this.solicitante_existe = true
          buscarCredito(this.form.solicitud.a.id_identificacion, this.form.solicitud.a.id_persona).then(response => {
            this.form.creditos = response.data
          })
        }
      }).catch(() => {
        this.nombre = null
        this.solicitante_existe = false
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
        this.actualizarPersona(this.form.id_identificacion, this.form.id_persona)
      })
    },
    buscarCodeudor(c) {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersona(c.id_identificacion, c.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          c.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
        }
      }).catch(() => {
        c.nombre = null
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
        this.actualizarPersona(c.id_identificacion, c.id_persona)
      })
    },
    actualizarPersona(id_identificacion, id_persona) {
      this.id_identificacion = id_identificacion
      this.id_persona = id_persona
      this.dialogPersonaVisible = true
    },
    borrarCodeudor(didx) {
      this.$confirm('Seguro de Borrar al Codeudor. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.codeudores.splice(didx, 1)
        var i = 1
        this.form.codeudores.forEach(c => {
          c.consecutivo = i
          i++
        })
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    onAddCodeudor() {
      const codeudor = {
        consecutivo: null,
        id_identificacion: null,
        id_persona: null,
        nombre: null,
        es_codeudor: null,
        es_conyuge: null
      }
      const csc = this.form.codeudores.length + 1
      codeudor.consecutivo = parseInt(csc)
      this.form.codeudores.push(codeudor)
    },
    onAddCredito() {
      const credito = {
        consecutivo: null,
        cuota_mensual: null,
        entidad: null,
        es_descuento: null,
        estado: null,
        fecha_capital: null,
        fecha_interes: null,
        id_colocacion: null,
        id_identificacion: this.form.solicitud.a.id_identificacion,
        id_persona: this.form.solicitud.a.id_persona,
        id_solicitud: null,
        saldo: null,
        tipo: null,
        valor_inicial: null,
        vencimiento: null
      }
      const csc = this.form.creditos.length + 1
      credito.consecutivo = parseInt(csc)
      this.form.creditos.push(credito)
    },
    onRemoveCodeudor(id) {
      this.form.codeudores.splice(id - 1, 1)
    },
    onGuardarSolicitud() {
      this.dialogPersonaVisible = false
      this.form.retefuente = this.form.retefuente ? 1 : 0
      this.form.cabezafamilia = this.form.cabezafamilia ? 1 : 0
      guardarSolicitud(this.form).then(response => {
        if (response.status === 200) {
          this.$message({
            message: 'Información guardada correctamente!',
            type: 'success'
          })
        } else {
          this.$message({
            message: 'No se pudo guardar la información, por favor verifique los datos.',
            type: 'warning'
          })
        }
      }).catch(error => {
        this.$message.error('Se presentó error guardando persona: ' + error)
      })
    }
  }
}
</script>
