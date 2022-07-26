# Tycoon - Online-Poker decision support system
[![Feature Requests](https://img.shields.io/github/issues/microsoft/vscode/feature-request.svg)](https://github.com/AdriRRP/tycoon/issues?q=is%3Aopen+is%3Aissue+label%3Afeature-request+sort%3Areactions-%2B1-desc)

Tycoon is a prototype decision support system for playing poker online. It is the result of my final degree project and its main objective is to try to extract knowledge in stochastic environments and with imperfect information, such as No Limit Texas Holdem Poker.

As knowledge extraction methodology, the one described in the papar "The KDD process for extracting useful knowledge from volumes of data" (Usama M. Fayyad, Gregory Piatetsky-Shapiro and P. Smyth, [1996](https://dl.acm.org/doi/10.1145/240455.240464))
is used.

[Here KDD diagram]

This project provides a cloud agnostic infrastructure for the collection, pre-processing and transformation of data, as well as the phase of knowledge extraction, construction and publication of the model in the form of a microservice and final client to exploit the model.

## Prerequisites

Tycoon is intended to be built and run cloud agnostic, so the only prerequisite is to have a Kubernetes >= 1.22 compatible cluster with the following apps installed:

- [MetalLB](https://metallb.universe.tf/) or any other kubernetes load balancer
- [Traefik v2](https://traefik.io/traefik/) edge router
- [cert-manager](https://cert-manager.io/) for certificate managemet
- A storage class that allows the dynamic creation of persistent volumes
- A private docker-registry deployed in the cluster

It is highly recommended to also have a guest machine (either physical, virtual or containerized) with the following programs installed:

- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [MinIO client](https://docs.min.io/docs/minio-client-complete-guide.html)

## Project structure

## Building Tycoon

### Infrastructure deployment

### Data ingestion

### Data preprocessing and transformation

### Model construction and calibration

### Model deployment

### Client deployment

## Test

## Documentation

## Troubleshooting
