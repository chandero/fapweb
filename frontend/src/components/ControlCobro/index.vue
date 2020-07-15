<template>
  <el-container>
    <el-header>
      <h3>Colocacion: {{ colocacion }}</h3>
    </el-header>
    <el-main>
      <el-form v-model="controlcobro">
        <el-row>
          <el-col :span="4">
            <el-form-item label="Fecha Observación">
              <el-date-picker v-model="controlcobro.fecha_observacion" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="Es Compromiso">
              <el-checkbox v-model="controlcobro.es_compromiso" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="Fecha Compromiso">
              <el-date-picker v-model="controlcobro.fecha_compromiso" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="Observación">
              <el-input :rows="4" v-model="controlcobro.observacion" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4">
            <el-button type="primary" @click="agregar">Agregar</el-button>
          </el-col>
          <el-col :span="16">
            &nbsp;
          </el-col>
          <el-col :span="4">
            <el-button type="error" @click="cerrar">Cerrar</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>

import { agregarControlCobro } from '@/api/controlcobro'

export default {
  name: 'ControlCobroComponent',
  props: {
    colocacion: {
      type: String,
      required: true
    },
    agencia: {
      type: Number,
      required: true
    },
    controlcobro: {
      type: Object,
      required: false,
      default: function() {
        return {
          fecha_observacion: new Date(),
          es_compromiso: false,
          fecha_compromiso: null,
          observacion: null,
          es_observacion: false
        }
      }
    }
  },
  data() {
    return {
      labelPostion: 'top'
    }
  },
  methods: {
    agregar() {
      this.controlcobro.id_agencia = this.agencia
      this.controlcobro.id_colocacion = this.colocacion
      if (this.controlcobro.es_compromiso) {
        this.controlcobro.es_observacion = 0
      } else {
        this.controlcobro.es_observacion = 1
      }
      this.controlcobro.es_compromiso = this.controlcobro.es_compromiso ? 1 : 0
      agregarControlCobro(this.controlcobro).then(() => {
        this.$emit('savedEvent', true)
      }).catch(() => {
        this.$emit('savedEvent', false)
      })
    },
    cerrar() {
      this.$emit('savedEvent', false)
    }
  }
}

</script>
