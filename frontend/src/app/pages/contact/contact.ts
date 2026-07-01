import { Component } from '@angular/core';
import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

@Component({
  selector: 'app-contact',
  imports: [Header, Footer],
  templateUrl: './contact.html',
  styleUrl: './contact.scss'
})
export class Contact {}
