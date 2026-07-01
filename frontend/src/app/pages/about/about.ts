import { Component } from '@angular/core';
import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

@Component({
  selector: 'app-about',
  imports: [Header, Footer],
  templateUrl: './about.html',
  styleUrl: './about.scss'
})
export class About {}
