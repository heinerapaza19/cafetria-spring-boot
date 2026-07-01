import { Component } from '@angular/core';
import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reservation',
  imports: [Header, Footer, FormsModule],
  templateUrl: './reservation.html',
  styleUrl: './reservation.scss'
})
export class Reservation {
  form = { nombre: '', email: '', fecha: '', hora: '', personas: 2, mensaje: '' };
  enviado = false;

  submit() { this.enviado = true; }
}
