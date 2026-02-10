# Getting Started

## Overview

REST microservice designed to manage multi-currency balances. This service provides functionalities for checking balances, depositing funds, debiting accounts, and performing currency exchanges using fixed internal rates.

## Quick Start

### Authentication

This API uses Bearer Authentication with JWT. All requests must include the Authorization: Bearer <token> header.

### Default Credentials

If the database is empty on startup, the system automatically loads initial data for testing:

Default users:

admin/password123

user1/password123

user2/passsword123

Base URL Development: /api/v1

### Features & Endpoints

#### Account Operations 
GET/accounts/{id}/balances  

Retrieve a list of all currency balances for a specific account.

####Transaction Operations

POST/accounts/{id}/add

Add funds to a specific currency balance.

POST/accounts/{id}/debit

Withdraw funds from a specific currency balance.

POST/accounts/{id}/exchange

Convert funds from one currency to another (EUR, USD, GBP, SEK).

#### Supported Currencies

The service currently supports the following currency codes:EUR, USD, GBP, SEK

